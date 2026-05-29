package com.finwise.finwise.transaction;

import com.finwise.finwise.account.Account;
import com.finwise.finwise.account.AccountRepository;
import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.category.Category;
import com.finwise.finwise.category.CategoryRepository;
import com.finwise.finwise.shared.exception.AccountNotFoundException;
import com.finwise.finwise.shared.exception.CategoryNotFoundException;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import com.finwise.finwise.transaction.dto.TransactionRequest;
import com.finwise.finwise.transaction.dto.TransactionResponse;
import com.finwise.finwise.shared.exception.TransactionNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Account account = accountRepository.findByIdAndUser(request.accountId(), user)
                .orElseThrow(AccountNotFoundException::new);

        Category category = categoryRepository.findByIdAndUser(request.categoryId(), user)
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
    public List<TransactionResponse> listByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        return transactionRepository.findByAccountUserOrderByDateDesc(user).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    private void applyToBalance(Account account, TransactionType type, BigDecimal amount) {
        BigDecimal current = account.getBalance();

        if (type == TransactionType.INCOME) {
            account.setBalance(current.add(amount));
        } else {
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

    private Transaction getOwnedTransaction(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        return transactionRepository.findByIdAndAccountUser(id, user)
                .orElseThrow(TransactionNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getById(String email, Long id){
        return TransactionResponse.from(getOwnedTransaction(email, id));
    }

    @Transactional
    public void delete(String email, Long id){
        Transaction transaction = getOwnedTransaction(email, id);
        Account account = transaction.getAccount();

        revertFromBalance(account, transaction.getType(), transaction.getAmount());

        transactionRepository.delete(transaction);
        accountRepository.save(account);
    }

    @Transactional
    public TransactionResponse update(String email, Long id, TransactionRequest request){
        Transaction transaction = getOwnedTransaction(email, id);
        User user = userRepository.findByEmail(email)
            .orElseThrow(InvalidCredentialsException::new);

        Category category = categoryRepository.findByIdAndUser(request.categoryId(), user)
            .orElseThrow(CategoryNotFoundException::new);

        Account oldAccount = transaction.getAccount();
        Account newAccount = accountRepository.findByIdAndUser(request.accountId(), user)
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
