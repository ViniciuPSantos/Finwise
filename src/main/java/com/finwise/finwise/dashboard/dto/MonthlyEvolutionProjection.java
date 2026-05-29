package com.finwise.finwise.dashboard.dto;

import java.math.BigDecimal;

public interface MonthlyEvolutionProjection {
    Integer getYear();
    Integer getMonth();
    BigDecimal getTotalIncome();
    BigDecimal getTotalExpense();
}
