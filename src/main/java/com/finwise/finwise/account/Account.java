package com.finwise.finwise.account;

import com.finwise.finwise.auth.User;
import com.finwise.finwise.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
