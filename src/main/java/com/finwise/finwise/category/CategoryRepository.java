package com.finwise.finwise.category;

import com.finwise.finwise.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserAndDeletedAtIsNull(User user);

    Optional<Category> findByIdAndUserAndDeletedAtIsNull(Long id, User user);

    Optional<Category> findByNameAndUserAndDeletedAtIsNull(String name, User user);
}
