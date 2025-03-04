package com.shitcode.demo1.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.ip2location.IP2Location;

@Configuration
@Scope("application")
public class Ip2LocationConfig {
    private final String DB_FILE = "ip2location/IP2LOCATION-LITE-DB1.bin";

    private final ResourceLoader resourceLoader;

    public Ip2LocationConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public IP2Location ip2Location() throws IOException {
        // Load the file from resources
        Resource resource = resourceLoader.getResource("classpath:" + DB_FILE);
        IP2Location ip2Location = new IP2Location();

        // Set the database file path
        ip2Location.Open(resource.getFile().getAbsolutePath());
        return ip2Location;
    }
}
