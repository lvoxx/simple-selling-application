package com.shitcode.demo1.annotation.validation.impl;

import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.annotation.validation.ValidVideo;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VideoFileValidator implements ConstraintValidator<ValidVideo, MultipartFile> {

    @Override
    public void initialize(ValidVideo constraintAnnotation) {

    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

        boolean result = true;

        String contentType = multipartFile.getContentType();
        if (!isSupportedContentType(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Only x-msvideo, quicktime, mpeg or mp4 videos are allowed.")
                    .addConstraintViolation();

            result = false;
        }

        return result;
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("video/mp4")
                || contentType.equals("video/mpeg")
                || contentType.equals("video/quicktime")
                || contentType.equals("video/x-msvideo");
    }
}