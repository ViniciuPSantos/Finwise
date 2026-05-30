package com.finwise.finwise.budget;

import com.finwise.finwise.auth.User;
import com.finwise.finwise.category.Category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);

    Optional<Budget> findByIdAndUser(Long id, User user);

    Optional<Budget> findByUserAndCategoryAndYearAndMonth(
            User user, Category category, Integer year, Integer month);

    List<Budget> findByUserAndYearAndMonth(User user, Integer year, Integer month);
}
