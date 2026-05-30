package com.finwise.finwise.shared.exception;

public class BudgetNotFoundException extends ResourceNotFoundException {
    public BudgetNotFoundException(){
        super("Budget not found");
    }
}
