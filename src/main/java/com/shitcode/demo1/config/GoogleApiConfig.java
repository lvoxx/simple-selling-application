package com.shitcode.demo1.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.google.api.services.drive.DriveScopes;
import com.google.auth.oauth2.GoogleCredentials;

@Configuration
public class GoogleApiConfig {
    private final ResourceLoader resourceLoader;
    private String googleCredentialsPath;

    public GoogleApiConfig(ResourceLoader resourceLoader, @Value("${googleapi.path}") String googleCredentialsPath) {
        this.resourceLoader = resourceLoader;
        this.googleCredentialsPath = googleCredentialsPath;
    }

    @Bean
    public GoogleCredentials googleCredentials() throws FileNotFoundException, IOException {
        // Load credentials from classpath resource
        var resource = resourceLoader.getResource(googleCredentialsPath);
        if (!resource.exists()) {
            throw new FileNotFoundException("Could not find Google credentials file: " + googleCredentialsPath);
        }
        return GoogleCredentials
                .fromStream(resource.getInputStream())
                .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
    }
}