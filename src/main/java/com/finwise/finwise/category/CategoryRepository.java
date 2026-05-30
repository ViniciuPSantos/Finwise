package com.finwise.finwise.category;

import com.finwise.finwise.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);

    Optional<Category> findByIdAndUser(Long id, User user);

    Optional<Category> findByNameAndUser(String name, User user);
}
