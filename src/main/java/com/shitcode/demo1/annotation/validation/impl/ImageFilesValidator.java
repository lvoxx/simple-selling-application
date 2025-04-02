package com.shitcode.demo1.annotation.validation.impl;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.annotation.validation.ValidImages;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageFilesValidator implements ConstraintValidator<ValidImages, List<MultipartFile>> {

    private static final Set<String> ALLOWED_FORMATS = Set.of("image/jpeg", "image/png", "application/pdf");

    private boolean nullable;

    @Override
    public void initialize(ValidImages constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            return nullable;
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty() || file.getContentType() == null) {
                return false;
            }
            if (!ALLOWED_FORMATS.contains(file.getContentType())) {
                return false;
            }
        }
        return true;
    }
}