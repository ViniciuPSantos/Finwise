package com.finwise.finwise.recurring;

import com.finwise.finwise.account.Account;
import com.finwise.finwise.account.AccountRepository;
import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.category.Category;
import com.finwise.finwise.category.CategoryRepository;
import com.finwise.finwise.recurring.dto.RecurringTransactionRequest;
import com.finwise.finwise.recurring.dto.RecurringTransactionResponse;
import com.finwise.finwise.shared.exception.AccountNotFoundException;
import com.finwise.finwise.shared.exception.CategoryNotFoundException;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import com.finwise.finwise.shared.exception.RecurringTransactionNotFoundException;
import com.finwise.finwise.transaction.TransactionService;
import com.finwise.finwise.transaction.dto.TransactionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecurringTransactionService {

    private final RecurringTransactionRepository recurringRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public RecurringTransactionService(RecurringTransactionRepository recurringRepository,
            AccountRepository accountRepository, CategoryRepository categoryRepository,
            UserRepository userRepository, TransactionService transactionService) {
        this.recurringRepository = recurringRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public RecurringTransactionResponse create(String email, RecurringTransactionRequest request) {
        User user = resolveUser(email);

        Account account = accountRepository.findByIdAndUserAndDeletedAtIsNull(request.accountId(), user)
                .orElseThrow(AccountNotFoundException::new);

        Category category = categoryRepository.findByIdAndUserAndDeletedAtIsNull(request.categoryId(), user)
                .orElseThrow(CategoryNotFoundException::new);

        RecurringTransaction recurring = new RecurringTransaction();
        recurring.setAmount(request.amount());
        recurring.setType(request.type());
        recurring.setDescription(request.description());
        recurring.setFrequency(request.frequency());
        recurring.setNextExecutionDate(request.startDate());
        recurring.setActive(true);
        recurring.setAccount(account);
        recurring.setCategory(category);
        recurring.setUser(user);

        return RecurringTransactionResponse.from(recurringRepository.save(recurring));
    }

    @Transactional(readOnly = true)
    public List<RecurringTransactionResponse> list(String email) {
        User user = resolveUser(email);
        return recurringRepository.findByUser(user).stream()
                .map(RecurringTransactionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecurringTransactionResponse getById(String email, Long id) {
        User user = resolveUser(email);
        return RecurringTransactionResponse.from(getOwned(id, user));
    }

    @Transactional
    public RecurringTransactionResponse update(String email, Long id, RecurringTransactionRequest request) {
        User user = resolveUser(email);
        RecurringTransaction recurring = getOwned(id, user);

        Account account = accountRepository.findByIdAndUserAndDeletedAtIsNull(request.accountId(), user)
                .orElseThrow(AccountNotFoundException::new);

        Category category = categoryRepository.findByIdAndUserAndDeletedAtIsNull(request.categoryId(), user)
                .orElseThrow(CategoryNotFoundException::new);

        recurring.setAmount(request.amount());
        recurring.setType(request.type());
        recurring.setDescription(request.description());
        recurring.setFrequency(request.frequency());
        recurring.setNextExecutionDate(request.startDate());
        recurring.setAccount(account);
        recurring.setCategory(category);

        return RecurringTransactionResponse.from(recurringRepository.save(recurring));
    }

    @Transactional
    public void delete(String email, Long id) {
        User user = resolveUser(email);
        recurringRepository.delete(getOwned(id, user));
    }

    @Transactional
    public void executeAll() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> due = recurringRepository
                .findByActiveTrueAndNextExecutionDateLessThanEqual(today);

        for (RecurringTransaction r : due) {
            try {
                String email = r.getUser().getEmail();
                TransactionRequest request = new TransactionRequest(
                        r.getAmount(), r.getType(), r.getDescription(),
                        r.getNextExecutionDate(), r.getAccount().getId(), r.getCategory().getId());
                transactionService.create(email, request);
                r.setNextExecutionDate(nextDate(r.getNextExecutionDate(), r.getFrequency()));
                recurringRepository.save(r);
            } catch (Exception ignored) {
            }
        }
    }

    private LocalDate nextDate(LocalDate current, RecurringFrequency frequency) {
        return switch (frequency) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
        };
    }

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
    }

    private RecurringTransaction getOwned(Long id, User user) {
        return recurringRepository.findByIdAndUser(id, user)
                .orElseThrow(RecurringTransactionNotFoundException::new);
    }
}
