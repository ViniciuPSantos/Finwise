package com.finwise.finwise.shared.exception;

public class CategoryNotFoundException extends ResourceNotFoundException {

    public CategoryNotFoundException() {
        super("Category not found");
    }
}
