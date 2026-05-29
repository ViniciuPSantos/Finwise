package com.finwise.finwise.transaction;

import com.finwise.finwise.auth.User;
import com.finwise.finwise.dashboard.dto.CategorySpendingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndAccountUser(Long id, User user);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.account.user = :user
            AND (:accountId IS NULL OR t.account.id = :accountId)
            AND(:categoryId IS NULL OR t.category.id = :categoryId)
            AND (:type IS NULL OR t.type = :type)
            AND (:startDate IS NULL OR t.date >= :startDate)
            AND (:endDate IS NULL OR t.date <= :endDate)
            """)
    Page<Transaction> findFiltered(
            @Param("user") User user,
            @Param("accountId") Long accountId,
            @Param("categoryId") Long categoryId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("""
            SELECT new com.finwise.finwise.dashboard.dto.CategorySpendingResponse(
                t.category.name, SUM(t.amount))
            FROM Transaction t
            WHERE t.account.user = :user
              AND t.type = com.finwise.finwise.transaction.TransactionType.EXPENSE
              AND t.date >= :startDate
              AND t.date <= :endDate
            GROUP BY t.category.name
            ORDER BY SUM(t.amount) DESC
            """)
    List<CategorySpendingResponse> sumExpensesByCategory(
            User user, LocalDate startDate, LocalDate endDate);
}
