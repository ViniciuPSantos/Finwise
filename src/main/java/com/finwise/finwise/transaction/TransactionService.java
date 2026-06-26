package com.finwise.finwise.transaction;

import com.finwise.finwise.account.Account;
import com.finwise.finwise.account.AccountRepository;
import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.category.Category;
import com.finwise.finwise.category.CategoryRepository;
import com.finwise.finwise.account.AccountType;
import com.finwise.finwise.shared.exception.AccountNotFoundException;
import com.finwise.finwise.shared.exception.InsufficientBalanceException;
import com.finwise.finwise.shared.dto.PageResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Pageable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import com.finwise.finwise.shared.exception.CategoryNotFoundException;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import com.finwise.finwise.transaction.dto.TransactionRequest;
import com.finwise.finwise.transaction.dto.TransactionResponse;
import com.finwise.finwise.shared.exception.TransactionNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository,
            CategoryRepository categoryRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionResponse create(String email, TransactionRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        Account account = accountRepository.findByIdAndUserAndDeletedAtIsNull(request.accountId(), user)
                .orElseThrow(AccountNotFoundException::new);

        Category category = categoryRepository.findByIdAndUserAndDeletedAtIsNull(request.categoryId(), user)
                .orElseThrow(CategoryNotFoundException::new);

        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setDescription(request.description());
        transaction.setDate(request.date());
        transaction.setAccount(account);
        transaction.setCategory(category);

        applyToBalance(account, request.type(), request.amount());

        Transaction saved = transactionRepository.save(transaction);
        accountRepository.save(account);

        return TransactionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> list(
            String email,
            Long accountId,
            Long categoryId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            String search,
            Pageable pageable) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        Page<Transaction> page = transactionRepository.findFiltered(
                user, accountId, categoryId, type, startDate, endDate, search, pageable);

        return PageResponse.from(page, TransactionResponse::from);
    }

    private void applyToBalance(Account account, TransactionType type, BigDecimal amount) {
        BigDecimal current = account.getBalance();

        if (type == TransactionType.INCOME) {
            account.setBalance(current.add(amount));
        } else {
            boolean blocksNegative = account.getType() == AccountType.CASH
                    || account.getType() == AccountType.CHECKING
                    || account.getType() == AccountType.SAVINGS;
            if (blocksNegative && current.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientBalanceException();
            }
            account.setBalance(current.subtract(amount));
        }
    }

    private void revertFromBalance(Account account, TransactionType type, BigDecimal amount) {
        BigDecimal current = account.getBalance();
        if (type == TransactionType.INCOME) {
            account.setBalance(current.subtract(amount));
        } else {
            account.setBalance(current.add(amount));
        }
    }

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
    }

    private Transaction getOwnedTransaction(String email, Long id) {
        User user = resolveUser(email);
        return transactionRepository.findByIdAndAccountUser(id, user)
                .orElseThrow(TransactionNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getById(String email, Long id) {
        return TransactionResponse.from(getOwnedTransaction(email, id));
    }

    @Transactional
    public void delete(String email, Long id) {
        Transaction transaction = getOwnedTransaction(email, id);
        Account account = transaction.getAccount();

        revertFromBalance(account, transaction.getType(), transaction.getAmount());

        transaction.setDeletedAt(java.time.Instant.now());
        transactionRepository.save(transaction);
        accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public byte[] export(String email, Long accountId, Long categoryId, TransactionType type,
            LocalDate startDate, LocalDate endDate, String search) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        List<Transaction> transactions = transactionRepository.findAllFiltered(
                user, accountId, categoryId, type, startDate, endDate, search);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter printer = new CSVPrinter(new PrintWriter(out),
                        CSVFormat.DEFAULT.builder()
                                .setHeader("id", "date", "type", "amount", "description", "account", "category")
                                .build())) {
            for (Transaction t : transactions) {
                printer.printRecord(t.getId(), t.getDate(), t.getType(), t.getAmount(),
                        t.getDescription(), t.getAccount().getName(), t.getCategory().getName());
            }
            printer.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate CSV export", e);
        }
    }

    @Transactional
    public TransactionResponse update(String email, Long id, TransactionRequest request) {
        Transaction transaction = getOwnedTransaction(email, id);
        User user = resolveUser(email);

        Category category = categoryRepository.findByIdAndUserAndDeletedAtIsNull(request.categoryId(), user)
                .orElseThrow(CategoryNotFoundException::new);

        Account oldAccount = transaction.getAccount();
        Account newAccount = accountRepository.findByIdAndUserAndDeletedAtIsNull(request.accountId(), user)
                .orElseThrow(AccountNotFoundException::new);

        revertFromBalance(oldAccount, transaction.getType(), transaction.getAmount());

        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setDescription(request.description());
        transaction.setDate(request.date());
        transaction.setAccount(newAccount);
        transaction.setCategory(category);

        applyToBalance(newAccount, request.type(), request.amount());

        transactionRepository.save(transaction);
        accountRepository.save(oldAccount);
        accountRepository.save(newAccount);

        return TransactionResponse.from(transaction);
    }
}
