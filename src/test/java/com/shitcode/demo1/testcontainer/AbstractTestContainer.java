package com.shitcode.demo1.testcontainer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

@SuppressWarnings("resource")
@Testcontainers
public abstract class AbstractTestContainer {
    @Container
    @ServiceConnection
    protected static PostgreSQLContainer<?> postgreSQLContainer;
 
    static {
        int containerPort = 5432;
        int localPort = 4321;
        DockerImageName postgres = DockerImageName.parse("postgres:17.4");
        postgreSQLContainer = new PostgreSQLContainer<>(postgres)
                .withDatabaseName("test")
                .withUsername("root")
                .withPassword("Te3tP4ssW@r$")
                .withReuse(true)
                .withExposedPorts(containerPort)
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(
                                new PortBinding(Ports.Binding.bindPort(localPort), new ExposedPort(containerPort)))));
    }
 
    @BeforeAll
    static void startContainer() {
        postgreSQLContainer.start();
    }
 
    @AfterAll
    static void stopContainer() {
        if (postgreSQLContainer != null) {
            postgreSQLContainer.close();
        }
    }
 
    @Test
    void canEstablishedPostgreSqlContainer() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }
}