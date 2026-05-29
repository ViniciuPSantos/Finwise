package com.finwise.finwise.transaction;

import com.finwise.finwise.transaction.dto.TransactionRequest;
import com.finwise.finwise.transaction.dto.TransactionResponse;
import com.finwise.finwise.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    public ResponseEntity<PageResponse<TransactionResponse>> list(
            @AuthenticationPrincipal String email,
        @RequestParam(required = false) Long accountId,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) TransactionType type,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @PageableDefault(size = 20, sort = "date") Pageable pageable) {
        
        PageResponse<TransactionResponse> result = transactionService.list(email, accountId, categoryId, type, startDate, endDate, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getById(email, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @AuthenticationPrincipal String email,
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(email, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        transactionService.delete(email, id);
        return ResponseEntity.noContent().build();
    }
}