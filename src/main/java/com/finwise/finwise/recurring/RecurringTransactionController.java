package com.finwise.finwise.recurring;

import com.finwise.finwise.recurring.dto.RecurringTransactionRequest;
import com.finwise.finwise.recurring.dto.RecurringTransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-transactions")
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;

    public RecurringTransactionController(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    @PostMapping
    public ResponseEntity<RecurringTransactionResponse> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody RecurringTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recurringTransactionService.create(email, request));
    }

    @GetMapping
    public ResponseEntity<List<RecurringTransactionResponse>> list(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(recurringTransactionService.list(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransactionResponse> getById(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        return ResponseEntity.ok(recurringTransactionService.getById(email, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecurringTransactionResponse> update(
            @AuthenticationPrincipal String email,
            @PathVariable Long id,
            @Valid @RequestBody RecurringTransactionRequest request) {
        return ResponseEntity.ok(recurringTransactionService.update(email, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        recurringTransactionService.delete(email, id);
        return ResponseEntity.noContent().build();
    }
}
