package com.finwise.finwise.imports.dto;

import java.util.List;

public record ImportResultResponse(
    int totalRows,
    int imported,
    int skipped,
    List<ImportErrorDetails> errors,
    List<String> createdCategories
){}
