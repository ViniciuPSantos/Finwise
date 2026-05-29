package com.finwise.finwise.category;

import com.finwise.finwise.category.dto.CategoryRequest;
import com.finwise.finwise.category.dto.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(email, request));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(categoryService.listByUser(email));

    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(email, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @AuthenticationPrincipal String email,
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(email, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        categoryService.delete(email, id);
        return ResponseEntity.noContent().build();
    }
}


