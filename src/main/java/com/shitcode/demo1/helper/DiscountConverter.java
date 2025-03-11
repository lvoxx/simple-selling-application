package com.shitcode.demo1.helper;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shitcode.demo1.utils.DiscountType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DiscountConverter implements AttributeConverter<List<DiscountType>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<DiscountType> discountTypes) {
        if (discountTypes == null || discountTypes.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(discountTypes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting list of DiscountType to JSON", e);
        }
    }

    @Override
    public List<DiscountType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(dbData,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, DiscountType.class));
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to list of DiscountType", e);
        }
    }
}