package com.finwise.finwise.transfer;

import com.finwise.finwise.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByUserOrderByDateDesc(User user);

    Optional<Transfer> findByIdAndUser(Long id, User user);
}
