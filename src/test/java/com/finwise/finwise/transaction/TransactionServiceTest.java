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
        when(accountRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(category));
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
        when(accountRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(category));
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
        when(accountRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        // espera a exceção, e o saldo NÃO deve mudar
        org.junit.jupiter.api.Assertions.assertThrows(
                com.finwise.finwise.shared.exception.AccountNotFoundException.class,
                () -> transactionService.create("test@finwise.com", request));
        assertThat(account.getBalance()).isEqualByComparingTo("1000.00");
    }
}
