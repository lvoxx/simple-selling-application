package com.shitcode.demo1.helper;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class PrettyPrintList {
    public static void printPrettyJson(List<Object[]> data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.registerModule(new JavaTimeModule()); // Fix for Java 8 date/time serialization

            // Convert List<Object[]> to a List of Lists for JSON serialization
            List<List<Object>> prettyData = data.stream()
                    .map(Arrays::asList) // Correctly convert Object[] to List<Object>
                    .toList();

            // Print Pretty JSON
            String jsonOutput = objectMapper.writeValueAsString(prettyData);
            System.out.println(jsonOutput.replace("], [", "],\n [")
                    .replace("] ]", "]\n]").replace("[ [", "[\n [")); // Force line breaks;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}