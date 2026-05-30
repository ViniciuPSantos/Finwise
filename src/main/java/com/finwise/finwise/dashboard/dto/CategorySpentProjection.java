package com.finwise.finwise.dashboard.dto;

import java.math.BigDecimal;

public interface CategorySpentProjection {
    Long getCategoryId();
    BigDecimal getTotalSpent();
} 
