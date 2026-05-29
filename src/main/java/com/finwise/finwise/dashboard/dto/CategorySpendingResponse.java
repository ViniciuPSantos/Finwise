package com.finwise.finwise.dashboard.dto;

import java.math.BigDecimal;

public record CategorySpendingResponse(
        String categoryName,
        BigDecimal total
) {}