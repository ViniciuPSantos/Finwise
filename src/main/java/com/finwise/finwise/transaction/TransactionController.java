package com.finwise.finwise.transaction;

import com.finwise.finwise.transaction.dto.TransactionRequest;
import com.finwise.finwise.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(transactionService.create(email, request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> list(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(transactionService.listByUser(email));
    }
}