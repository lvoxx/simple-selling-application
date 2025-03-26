package com.shitcode.demo1.init;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.CannotReadScriptException;
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import com.shitcode.demo1.utils.LogPrinter;

import jakarta.annotation.PostConstruct;

@Component
@Profile("!test")
public class SQLRunner implements CommandLineRunner {
    private static Logger log = LoggerFactory.getLogger(SQLRunner.class);

    private static final List<String> sqlMap = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public SQLRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    void setUp() {
        // Check if DataSource is present
        Optional.ofNullable(jdbcTemplate.getDataSource())
                .ifPresentOrElse(dataSource -> {
                    try (Connection connection = dataSource.getConnection()) {
                        if (connection.isValid(2)) { // Check if connection is valid within 2 seconds
                            LogPrinter.printLog(LogPrinter.Type.INFO, LogPrinter.Flag.START_UP,
                                    "Database connection established successfully.");
                            sqlMap.add("database/indexes.sql");
                            sqlMap.add("database/categories.sql");
                            sqlMap.add("database/products.sql");
                            sqlMap.add("database/discounts.sql");
                            sqlMap.add("database/product_interaction.sql");
                        } else {
                            LogPrinter.printLog(LogPrinter.Type.ERROR, LogPrinter.Flag.START_UP,
                                    "Database connection is not valid.");
                        }
                    } catch (SQLException e) {
                        LogPrinter.printLog(LogPrinter.Type.ERROR, LogPrinter.Flag.START_UP,
                                "Failed to establish a connection to the database: " + e.getMessage());
                    }
                },
                        () -> {
                            LogPrinter.printLog(LogPrinter.Type.ERROR, LogPrinter.Flag.START_UP,
                                    "No DataSource available. Skipping SQL execution.");
                        });
    }

    @SuppressWarnings("null")
    @Override
    public void run(String... args) throws Exception {

        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            for (String sql : sqlMap) {
                try {
                    Pattern pattern = Pattern.compile("database/(.*?).sql");
                    Matcher matcher = pattern.matcher(sql);
                    connection.createStatement().setQueryTimeout(300); // Timeout in seconds (5 minutes)
                    if (matcher.find()) {
                        // COMMENT THIS TO IGNORE GENERATE DUMMY
                        if (matcher.group(1).equalsIgnoreCase("product_interaction")) {
                            insertProductInteractionData(connection, sql);
                            continue;
                        }
                        // COMMENT THIS TO IGNORE GENERATE DUMMY
                        ScriptUtils.executeSqlScript(connection, new ClassPathResource(sql));
                        LogPrinter.printLog(LogPrinter.Type.INFO, LogPrinter.Flag.START_UP,
                                String.format("SQL script executed from initial database %s",
                                        matcher.group(1).toUpperCase()));
                    } else {
                        LogPrinter.printLog(LogPrinter.Type.ERROR, LogPrinter.Flag.START_UP,
                                String.format("Cannt find the initial database [%s]", sql));
                    }
                } catch (CannotReadScriptException | ScriptStatementFailedException e) {
                    log.error(e.getMessage());
                    log.error(e.getCause().toString());
                }
            }
        }
    }

    private void insertProductInteractionData(Connection connection, String sqlPath) {
        final int totalRows = 5_000_000;
        final int batchSize = 100_000;

        LogPrinter.printLog(LogPrinter.Type.INFO, LogPrinter.Flag.START_UP,
                String.format("Starting batch insert for %d rows in batches of %d",
                        totalRows, batchSize));

        IntStream.range(0, totalRows / batchSize).forEach(batch -> {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(sqlPath));
            LogPrinter.printLog(LogPrinter.Type.INFO, LogPrinter.Flag.START_UP,
                    String.format("Batch %d inserted successfully", batch + 1));
        });

        LogPrinter.printLog(LogPrinter.Type.INFO, LogPrinter.Flag.START_UP,
                String.format("Completed batch inserts for product_interaction table."));
    }

}
