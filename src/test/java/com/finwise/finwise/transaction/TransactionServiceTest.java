package com.finwise.finwise.transaction;

import com.finwise.finwise.account.Account;
import com.finwise.finwise.account.AccountRepository;
import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.category.Category;
import com.finwise.finwise.category.CategoryRepository;
import com.finwise.finwise.transaction.dto.TransactionRequest;
import com.finwise.finwise.transaction.dto.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Account account;
    private Category category;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@finwise.com");

        account = new Account();
        account.setId(1L);
        account.setName("Nubank");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(user);

        category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setUser(user);
    }

    @Test
    void shouldDecreasedBalanceWhenCreatingExepense() {
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("50.00"),
                TransactionType.EXPENSE,
                "Almoço",
                LocalDate.of(2026, 5, 28),
                1L,
                1L);

        when(userRepository.findByEmail("test@finwise.com")).thenReturn(Optional.of(user));
        when(accountRepository.findByIdAndUserAndDeletedAtIsNull(1L, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserAndDeletedAtIsNull(1L, user)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.create("test@finwise.com", request);

        assertThat(account.getBalance()).isEqualByComparingTo("950.00");
        assertThat(response.amount()).isEqualByComparingTo("50.00");
        assertThat(response.type()).isEqualTo(TransactionType.EXPENSE);
    }

    @Test
    void shouldIncreaseBalanceWhenCreatingIncome() {
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("200.00"),
                TransactionType.INCOME,
                "Salário",
                LocalDate.of(2026, 5, 28),
                1L,
                1L);

        when(userRepository.findByEmail("test@finwise.com")).thenReturn(Optional.of(user));
        when(accountRepository.findByIdAndUserAndDeletedAtIsNull(1L, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserAndDeletedAtIsNull(1L, user)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.create("test@finwise.com", request);

        // receita de 200 sobre saldo 1000 → 1200
        assertThat(account.getBalance()).isEqualByComparingTo("1200.00");
    }

    @Test
    void shouldThrowWhenAccountNotOwned() {
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("50.00"),
                TransactionType.EXPENSE,
                "Almoço",
                LocalDate.of(2026, 5, 28),
                99L, // conta que não é do usuário
                1L);

        when(userRepository.findByEmail("test@finwise.com")).thenReturn(Optional.of(user));
        when(accountRepository.findByIdAndUserAndDeletedAtIsNull(99L, user)).thenReturn(Optional.empty());

        // espera a exceção, e o saldo NÃO deve mudar
        org.junit.jupiter.api.Assertions.assertThrows(
                com.finwise.finwise.shared.exception.AccountNotFoundException.class,
                () -> transactionService.create("test@finwise.com", request));
        assertThat(account.getBalance()).isEqualByComparingTo("1000.00");
    }

    private Transaction existingExpense(BigDecimal amount) {
        Transaction t = new Transaction();

        t.setId(10L);
        t.setAmount(amount);
        t.setType(TransactionType.EXPENSE);
        t.setDescription("almoço");
        t.setDate(LocalDate.of(2026, 5, 28));
        t.setAccount(account);
        t.setCategory(category);
        return t;
    }

    @Test
    void shouldAdjustBalanceWhenUpdatingAmount() {
        Transaction existing = existingExpense(new BigDecimal("50.00"));

        TransactionRequest request = new TransactionRequest(
                new BigDecimal("70.00"), // novo valor
                TransactionType.EXPENSE, // mesmo tipo
                "Almoço caro",
                LocalDate.of(2026, 5, 28),
                1L, // mesma conta
                1L);

        when(userRepository.findByEmail("test@finwise.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findByIdAndAccountUser(10L, user)).thenReturn(Optional.of(existing));
        when(accountRepository.findByIdAndUserAndDeletedAtIsNull(1L, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserAndDeletedAtIsNull(1L, user)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        transactionService.update("test@finwise.com", 10L, request);

        // Assert: reverte a despesa antiga (+50) e aplica a nova (-70) sobre o saldo
        // 1000
        // 1000 + 50 - 70 = 980
        assertThat(account.getBalance()).isEqualByComparingTo("980.00");
    }

    @Test
    void shouldAdjustBalanceWhenChangingType() {
        Transaction existing = existingExpense(new BigDecimal("50.00"));

        TransactionRequest request = new TransactionRequest(
                new BigDecimal("50.00"), // mesmo valor
                TransactionType.INCOME,
                "Estorno",
                LocalDate.of(2026, 5, 28),
                1L,
                1L);

        when(userRepository.findByEmail("test@finwise.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findByIdAndAccountUser(10L, user)).thenReturn(Optional.of(existing));
        when(accountRepository.findByIdAndUserAndDeletedAtIsNull(1L, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserAndDeletedAtIsNull(1L, user)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.update("test@finwise.com", 10L, request);

        // reverte despesa antiga de 50 (+50) e aplica receita nova de 50 (+50)
        // 1000 + 50 + 50 = 1100
        assertThat(account.getBalance()).isEqualByComparingTo("1100.00");
    }

    @Test
    void shouldMoveBalanceBetweenAccountsWhenChangingAccount() {
        // segunda conta, com saldo próprio
        Account newAccount = new Account();
        newAccount.setId(2L);
        newAccount.setName("Carteira");
        newAccount.setBalance(new BigDecimal("500.00"));
        newAccount.setUser(user);

        Transaction existing = existingExpense(new BigDecimal("50.00"));
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("50.00"),
                TransactionType.EXPENSE,
                "Almoço",
                LocalDate.of(2026, 5, 28),
                2L, // conta MUDOU: vai para a conta 2
                1L);

        when(userRepository.findByEmail("test@finwise.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findByIdAndAccountUser(10L, user)).thenReturn(Optional.of(existing));
        when(accountRepository.findByIdAndUserAndDeletedAtIsNull(2L, user)).thenReturn(Optional.of(newAccount));
        when(categoryRepository.findByIdAndUserAndDeletedAtIsNull(1L, user)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.update("test@finwise.com", 10L, request);

        // conta ANTIGA recebe a devolução da despesa: 1000 + 50 = 1050
        assertThat(account.getBalance()).isEqualByComparingTo("1050.00");
        // conta NOVA recebe a aplicação da despesa: 500 - 50 = 450
        assertThat(newAccount.getBalance()).isEqualByComparingTo("450.00");
    }

    @Test
    void shouldRevertBalanceWhenDeleting() {
        Transaction existing = existingExpense(new BigDecimal("50.00"));

        when(userRepository.findByEmail("test@finwise.com")).thenReturn(Optional.of(user));
        when(transactionRepository.findByIdAndAccountUser(10L, user)).thenReturn(Optional.of(existing));

        transactionService.delete("test@finwise.com", 10L);

        // deletar uma despesa de 50 devolve +50 ao saldo: 1000 + 50 = 1050
        assertThat(account.getBalance()).isEqualByComparingTo("1050.00");
    }
}
