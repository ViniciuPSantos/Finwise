package com.finwise.finwise.transfer;

import com.finwise.finwise.transfer.dto.TransferRequest;
import com.finwise.finwise.transfer.dto.TransferResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferService.create(email, request));
    }

    @GetMapping
    public ResponseEntity<List<TransferResponse>> list(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(transferService.list(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferResponse> getById(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        return ResponseEntity.ok(transferService.getById(email, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        transferService.delete(email, id);
        return ResponseEntity.noContent().build();
    }
}
