package com.shitcode.demo1.service;

public interface MailService {
    void sendActivationEmail(String toEmail, String token, String username);

    void sendEmail(String to, String subject, String content);
}