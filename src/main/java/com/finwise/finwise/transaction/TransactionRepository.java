package com.finwise.finwise.transaction;

import com.finwise.finwise.auth.User;
import com.finwise.finwise.dashboard.dto.CategorySpendingResponse;
import com.finwise.finwise.dashboard.dto.CategorySpentProjection;
import com.finwise.finwise.dashboard.dto.IncomeExpenseProjection;
import com.finwise.finwise.dashboard.dto.MonthlyEvolutionProjection;

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
            AND (:categoryId IS NULL OR t.category.id = :categoryId)
            AND (:type IS NULL OR t.type = :type)
            AND (:startDate IS NULL OR t.date >= :startDate)
            AND (:endDate IS NULL OR t.date <= :endDate)
            AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Transaction> findFiltered(
            @Param("user") User user,
            @Param("accountId") Long accountId,
            @Param("categoryId") Long categoryId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("search") String search,
            Pageable pageable);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.account.user = :user
            AND (:accountId IS NULL OR t.account.id = :accountId)
            AND (:categoryId IS NULL OR t.category.id = :categoryId)
            AND (:type IS NULL OR t.type = :type)
            AND (:startDate IS NULL OR t.date >= :startDate)
            AND (:endDate IS NULL OR t.date <= :endDate)
            AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY t.date DESC
            """)
    List<Transaction> findAllFiltered(
            @Param("user") User user,
            @Param("accountId") Long accountId,
            @Param("categoryId") Long categoryId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("search") String search);

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

    @Query("""
            SELECT
                SUM(CASE WHEN t.type = com.finwise.finwise.transaction.TransactionType.INCOME
                    THEN t.amount ELSE 0 END) AS totalIncome,
                SUM(CASE WHEN t.type = com.finwise.finwise.transaction.TransactionType.EXPENSE
                    THEN t.amount ELSE 0 END) AS totalExpense
            FROM Transaction t
            WHERE t.account.user = :user
              AND t.date >= :startDate
              AND t.date <= :endDate
            """)
    IncomeExpenseProjection sumByType(User user, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT
                YEAR(t.date) AS year,
                MONTH(t.date) AS month,
                SUM(CASE WHEN t.type = com.finwise.finwise.transaction.TransactionType.INCOME
                    THEN t.amount ELSE 0 END) AS totalIncome,
                SUM(CASE WHEN t.type = com.finwise.finwise.transaction.TransactionType.EXPENSE
                    THEN t.amount ELSE 0 END) AS totalExpense
            FROM Transaction t
            WHERE t.account.user = :user
              AND t.date >= :startDate
              AND t.date <= :endDate
            GROUP BY YEAR(t.date), MONTH(t.date)
            ORDER BY YEAR(t.date), MONTH(t.date)
            """)
    List<MonthlyEvolutionProjection> sumByMonth(User user, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT t.category.id AS categoryId, SUM(t.amount) AS totalSpent
            FROM Transaction t
            WHERE t.account.user = :user
              AND t.type = com.finwise.finwise.transaction.TransactionType.EXPENSE
              AND YEAR(t.date) = :year
              AND MONTH(t.date) = :month
            GROUP BY t.category.id
            """)
    List<CategorySpentProjection> sumExpensesByCategoryIdForMonth(
            User user, Integer year, Integer month);
}
