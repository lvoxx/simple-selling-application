package com.shitcode.demo1.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shitcode.demo1.properties.LvoxxServerConfigData;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfiguration {
        private final LvoxxServerConfigData lvoxxServerConfigData;

        public OpenAPIConfiguration(LvoxxServerConfigData lvoxxServerConfigData) {
                this.lvoxxServerConfigData = lvoxxServerConfigData;
        }

        @Bean
        OpenAPI openAPI() {
                Server devServer = new Server()
                                .url(lvoxxServerConfigData.getDevServer().getBaseUrl())
                                .description(lvoxxServerConfigData.getDevServer().getDescription());

                Server prodServer = new Server()
                                .url(lvoxxServerConfigData.getProdServer().getBaseUrl())
                                .description(lvoxxServerConfigData.getProdServer().getDescription());

                Contact contact = new Contact()
                                .name(lvoxxServerConfigData.getContact().getName())
                                .email(lvoxxServerConfigData.getContact().getEmail())
                                .url(lvoxxServerConfigData.getContact().getUrl());

                License license = new License()
                                .name(lvoxxServerConfigData.getLicense().getName())
                                .url(lvoxxServerConfigData.getLicense().getUrl());

                Info info = new Info()
                                .title(lvoxxServerConfigData.getInfo().getTitle())
                                .version(lvoxxServerConfigData.getInfo().getVersion())
                                .description(lvoxxServerConfigData.getInfo().getDescription())
                                .contact(contact)
                                .license(license);

                ExternalDocumentation externalDocs = new ExternalDocumentation()
                                .description(lvoxxServerConfigData.getExternalDocumentation().getDescription())
                                .url(lvoxxServerConfigData.getExternalDocumentation().getUrl());

                return new OpenAPI()
                                .info(info)
                                .servers(List.of(devServer, prodServer))
                                .externalDocs(externalDocs);
        }
}
