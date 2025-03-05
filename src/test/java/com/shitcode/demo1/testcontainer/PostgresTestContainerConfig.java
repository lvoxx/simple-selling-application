package com.shitcode.demo1.testcontainer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("resource")
@TestConfiguration
public abstract class PostgresTestContainerConfig {

    static PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER;

    static {
        DockerImageName postgres = DockerImageName.parse("postgres:17.4-alpine");
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(postgres)
                .withDatabaseName("test")
                .withUsername("root")
                .withPassword("Te3tP4ssW@r$")
                .withReuse(true)
                .waitingFor(new LogMessageWaitStrategy()
                        .withRegEx(".*database system is ready to accept connections.*\\s")
                        .withTimes(2).withStartupTimeout(Duration.of(60L, ChronoUnit.SECONDS)));
        POSTGRE_SQL_CONTAINER.start();
    }

    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        return POSTGRE_SQL_CONTAINER;
    }
}