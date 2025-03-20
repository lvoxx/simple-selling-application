package com.shitcode.demo1.utils;

/**
 * Enum representing different logging models in the application.
 * This is used to categorize log messages based on their module.
 */
public enum LoggingModel {
    /** Logs related to the Controller layer. */
    CONTROLLER("CONTROLLER"),

    /** Logs related to the Service layer. */
    SERVICE("SERVICE"),

    /** Logs related to the Repository layer. */
    REPOSITORY("REPOSITORY"),

    /** Logs related to Aspect-Oriented Programming (AOP) aspects. */
    ASPECT("ASPECT"),

    /** Logs related to utility/helper methods. */
    UTILS("UTILS");

    private final String model;

    /**
     * Constructs a LoggingModel enum with the specified model name.
     * 
     * @param model The name of the logging model.
     */
    LoggingModel(String model) {
        this.model = model;
    }

    /**
     * Retrieves the model name of the logging category.
     * 
     * @return The model name as a string.
     */
    public String getModel() {
        return model;
    }
}