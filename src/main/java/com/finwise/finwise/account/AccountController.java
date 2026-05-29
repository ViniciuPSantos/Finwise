package com.finwise.finwise.account;

import com.finwise.finwise.account.dto.AccountRequest;
import com.finwise.finwise.account.dto.AccountResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(
        @AuthenticationPrincipal String email,
        @Valid @RequestBody AccountRequest request
    ){
        AccountResponse response = service.create(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> list(
        @AuthenticationPrincipal String email
    ){
        List<AccountResponse> accounts = service.listByUser(email);
        return ResponseEntity.ok(accounts);
    }
}
