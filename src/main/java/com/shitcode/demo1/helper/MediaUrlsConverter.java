package com.shitcode.demo1.helper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MediaUrlsConverter implements AttributeConverter<List<String>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> images) {
        return (images == null || images.isEmpty()) ? null 
               : images.stream().collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return (dbData == null || dbData.isBlank()) ? List.of() 
               : Arrays.asList(dbData.split(SEPARATOR));
    }
}