package com.shitcode.demo1.config;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.lang.NonNull;
import org.yaml.snakeyaml.Yaml;

@Configuration
public class MessagesConfig {
    @Bean
    public MessageSource messageSource() {
        return new YamlMessageSource("message/messages.yaml");
    }

    static class YamlMessageSource extends AbstractMessageSource {

        private final Map<String, Object> messages;

        public YamlMessageSource(String yamlFile) {
            Yaml yaml = new Yaml();
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(yamlFile)) {
                messages = yaml.load(inputStream);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load YAML message file", e);
            }
        }

        @Override
        protected MessageFormat resolveCode(@NonNull String code, @NonNull Locale locale) {
            String message = resolveNestedKey(messages, code);
            return (message != null) ? new MessageFormat(message, locale) : null;
        }

        @SuppressWarnings("unchecked")
        private String resolveNestedKey(Map<String, Object> map, String key) {
            String[] keys = key.split("\\.");
            Object value = map;
            for (String k : keys) {
                if (value instanceof Map) {
                    value = ((Map<String, Object>) value).get(k);
                } else {
                    return null;
                }
            }
            return (value instanceof String) ? (String) value : null;
        }
    }
}
