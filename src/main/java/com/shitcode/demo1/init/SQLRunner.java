package com.shitcode.demo1.init;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                            log.info("Database connection established successfully.");
                            sqlMap.add("database/categories.sql");
                            sqlMap.add("database/products.sql");
                        } else {
                            log.error("Database connection is not valid.");
                        }
                    } catch (SQLException e) {
                        log.error("Failed to establish a connection to the database: " + e.getMessage());
                    }
                },
                        () -> {
                            log.error("No DataSource available. Skipping SQL execution.");
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

                    if (matcher.find()) {
                        ScriptUtils.executeSqlScript(connection, new ClassPathResource(sql));
                        log.info("SQL script executed from initial database {}", matcher.group(1).toUpperCase());
                    } else {
                        log.error("Cannt find the initial database [{}]", sql);
                    }
                } catch (CannotReadScriptException | ScriptStatementFailedException e) {
                    log.error(e.getMessage());
                    log.error(e.getCause().toString());
                }
            }
        }
    }

}
