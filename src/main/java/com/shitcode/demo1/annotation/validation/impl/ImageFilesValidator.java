package com.shitcode.demo1.annotation.validation.impl;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.annotation.validation.ValidImages;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageFilesValidator implements ConstraintValidator<ValidImages, List<MultipartFile>> {

    private static final Set<String> ALLOWED_FORMATS = Set.of("image/jpeg", "image/png", "application/pdf");

    @Override
    public boolean isValid(List<MultipartFile> multipartFiles, ConstraintValidatorContext context) {

        boolean result = true;

        for (MultipartFile file : multipartFiles) {
            String contentType = file.getContentType();
            if (!isSupportedContentType(contentType)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Only PNG or JPG images are allowed.")
                        .addConstraintViolation();

                result = false;
            }
        }

        return result;
    }

    private boolean isSupportedContentType(String contentType) {
        return ALLOWED_FORMATS.contains(contentType);
    }
}