package com.finwise.finwise.dashboard.dto;

import java.math.BigDecimal;

public interface IncomeExpenseProjection {
    BigDecimal getTotalIncome();
    BigDecimal getTotalExpense();
} 
