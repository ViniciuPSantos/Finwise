package com.finwise.finwise.account;

import com.finwise.finwise.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserAndDeletedAtIsNull(User user);

    Optional<Account> findByIdAndUserAndDeletedAtIsNull(Long id, User user);

    Optional<Account> findByNameAndUserAndDeletedAtIsNull(String name, User user);
}
