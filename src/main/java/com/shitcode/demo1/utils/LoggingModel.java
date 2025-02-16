package com.shitcode.demo1.utils;

public enum LoggingModel {
    CONTROLLER("CONTROLLER"), 
    SERVICE("SERVICE"), 
    REPOSITORY("REPOSITORY"), 
    ASPECT("ASPECT"), 
    UTILS("UTILS");

    private final String model;

    LoggingModel(String model) {
        this.model = model;
    }

    String getModel() {
        return model;
    }
}