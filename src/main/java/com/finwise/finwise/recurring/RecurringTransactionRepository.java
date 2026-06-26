package com.finwise.finwise.recurring;

import com.finwise.finwise.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    List<RecurringTransaction> findByUser(User user);

    Optional<RecurringTransaction> findByIdAndUser(Long id, User user);

    List<RecurringTransaction> findByActiveTrueAndNextExecutionDateLessThanEqual(LocalDate date);
}
