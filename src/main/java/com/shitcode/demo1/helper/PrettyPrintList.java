package com.shitcode.demo1.helper;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class PrettyPrintList {
    public static void printPrettyJson(List<Object[]> data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Convert List<Object[]> to a List of Lists for JSON serialization
            List<List<Object>> prettyData = data.stream()
                    .map(List::of)
                    .toList();

            // Print Pretty JSON
            String jsonOutput = objectMapper.writeValueAsString(prettyData);
            System.out.println(jsonOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
