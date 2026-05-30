package com.finwise.finwise.imports;

import com.finwise.finwise.account.Account;
import com.finwise.finwise.account.AccountRepository;
import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.category.Category;
import com.finwise.finwise.category.CategoryRepository;
import com.finwise.finwise.category.CategoryService;
import com.finwise.finwise.category.dto.CategoryRequest;
import com.finwise.finwise.imports.dto.ImportErrorDetail;
import com.finwise.finwise.imports.dto.ImportResultResponse;
import com.finwise.finwise.imports.dto.ParsedRow;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import com.finwise.finwise.transaction.TransactionService;
import com.finwise.finwise.transaction.TransactionType;
import com.finwise.finwise.transaction.dto.TransactionRequest;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ImportService {

    private final CsvParser csvParser;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ImportService(CsvParser csvParser,
            TransactionService transactionService,
            CategoryService categoryService,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.csvParser = csvParser;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public ImportResultResponse importCsv(String email, InputStream inputStream) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        List<ParsedRow> rows = csvParser.parse(inputStream);

        int imported = 0;
        List<ImportErrorDetail> errors = new ArrayList<>();
        Set<String> createdCategories = new LinkedHashSet<>();

        for (ParsedRow row : rows) {
            try {
                BigDecimal amount = new BigDecimal(row.amount());
                TransactionType type = TransactionType.valueOf(row.type().toUpperCase());
                LocalDate date = parseDate(row.date());

                Account account = accountRepository
                        .findByNameAndUser(row.account(), user)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Account '" + row.account() + "' not found"));

                Long categoryId = resolveOrCreateCategory(
                        email, user, row.category(), createdCategories);

                TransactionRequest request = new TransactionRequest(
                        amount, type, row.description(), date,
                        account.getId(), categoryId);

                transactionService.create(email, request);
                imported++;

            } catch (NumberFormatException e) {
                errors.add(new ImportErrorDetail(row.line(),
                        "Invalid amount: '" + row.amount() + "'"));
            } catch (IllegalArgumentException e) {
                errors.add(new ImportErrorDetail(row.line(), e.getMessage()));
            } catch (DateTimeParseException e) {
                errors.add(new ImportErrorDetail(row.line(),
                        "Invalid date: '" + row.date() + "'"));
            } catch (Exception e) {
                errors.add(new ImportErrorDetail(row.line(),
                        "Unexpected error: " + e.getMessage()));
            }
        }

        return new ImportResultResponse(
                rows.size(),
                imported,
                errors.size(),
                errors,
                new ArrayList<>(createdCategories));
    }

    private Long resolveOrCreateCategory(
            String email, User user, String name, Set<String> createdCategories) {

        return categoryRepository.findByNameAndUser(name, user)
                .map(Category::getId)
                .orElseGet(() -> {
                    var created = categoryService.create(email, new CategoryRequest(name));
                    createdCategories.add(name);
                    return created.id();
                });
    }

    private static final DateTimeFormatter BR_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private LocalDate parseDate(String raw) {
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(raw, BR_DATE);
        }
    }
}