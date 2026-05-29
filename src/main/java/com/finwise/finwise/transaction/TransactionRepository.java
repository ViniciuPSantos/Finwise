package com.finwise.finwise.transaction;

import com.finwise.finwise.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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
        Pageable pageable
    );

}
