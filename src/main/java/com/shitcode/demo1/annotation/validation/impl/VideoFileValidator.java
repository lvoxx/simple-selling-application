package com.shitcode.demo1.annotation.validation.impl;

import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.annotation.validation.ValidVideo;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VideoFileValidator implements ConstraintValidator<ValidVideo, MultipartFile> {

    private boolean nullable;

    @Override
    public void initialize(ValidVideo constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return nullable;
        }

        String contentType = multipartFile.getContentType();
        if (!isSupportedContentType(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Only x-msvideo, quicktime, mpeg or mp4 videos are allowed.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("video/mp4")
                || contentType.equals("video/mpeg")
                || contentType.equals("video/quicktime")
                || contentType.equals("video/x-msvideo");
    }
}