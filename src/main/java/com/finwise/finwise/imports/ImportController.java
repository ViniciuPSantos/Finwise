package com.finwise.finwise.imports;

import com.finwise.finwise.imports.dto.ImportResultResponse;
import com.finwise.finwise.shared.exception.InvalidCsvException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/imports")
public class ImportController {
    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value = "/csv", consumes = "multipart/form-data")
    public ImportResultResponse importCsv(
            @AuthenticationPrincipal String email,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidCsvException("File is empty");
        }

        try {
            return importService.importCsv(email, file.getInputStream());
        } catch (IOException e) {
            throw new InvalidCsvException("Could not read uploaded file");
        }
    }
}
