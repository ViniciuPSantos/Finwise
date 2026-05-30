package com.finwise.finwise.imports;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import com.finwise.finwise.shared.exception.InvalidCsvException;
import com.finwise.finwise.imports.dto.ParsedRow;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvParser {

    private static final String[] HEADERS =
            {"date", "amount", "type", "description", "account", "category"};

    public List<ParsedRow> parse(InputStream inputStream) {
        List<ParsedRow> rows = new ArrayList<>();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build();

        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser parser = CSVParser.parse(reader, format)) {

            for (CSVRecord record : parser) {
                int line = (int) record.getRecordNumber() + 1;
                rows.add(new ParsedRow(
                        line,
                        record.get("date"),
                        record.get("amount"),
                        record.get("type"),
                        record.get("description"),
                        record.get("account"),
                        record.get("category")
                ));
            }
        } catch (IOException e) {
            throw new InvalidCsvException("Could not read CSV file");
        }

        return rows;
    }
}
