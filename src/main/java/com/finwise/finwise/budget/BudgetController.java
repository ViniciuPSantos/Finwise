package com.finwise.finwise.budget;

import com.finwise.finwise.budget.dto.BudgetRequest;
import com.finwise.finwise.budget.dto.BudgetResponse;
import com.finwise.finwise.budget.dto.BudgetStatusResponse;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetResponse create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody BudgetRequest request) {
        return budgetService.create(email, request);
    }

    @GetMapping
    public List<BudgetResponse> list(@AuthenticationPrincipal String email) {
        return budgetService.list(email);
    }

    @GetMapping("/{id}")
    public BudgetResponse getById(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        return budgetService.getById(email, id);
    }

    @PutMapping("/{id}")
    public BudgetResponse update(
            @AuthenticationPrincipal String email,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        return budgetService.update(email, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        budgetService.delete(email, id);
    }

    @GetMapping("/status")
    public List<BudgetStatusResponse> status(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return budgetService.getBudgetStatus(email, year, month);
    }

}
