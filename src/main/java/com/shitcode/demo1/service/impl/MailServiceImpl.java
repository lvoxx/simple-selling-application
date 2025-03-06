package com.shitcode.demo1.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.google.errorprone.annotations.DoNotMock;
import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.properties.LvoxxServerConfigData;
import com.shitcode.demo1.properties.MailingConfigData;
import com.shitcode.demo1.service.MailService;
import com.shitcode.demo1.utils.LoggingModel;

@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
@DoNotMock
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final LvoxxServerConfigData serverConfigData;
    private final MailingConfigData mailingConfigData;

    public MailServiceImpl(JavaMailSender mailSender, LvoxxServerConfigData serverConfigData,
            MailingConfigData mailingConfigData) {
        this.mailSender = mailSender;
        this.serverConfigData = serverConfigData;
        this.mailingConfigData = mailingConfigData;
    }

    @Override
    public void sendActivationEmail(String toEmail, String token) throws Exception {
        // ${ActivationUrl} - active account link
        // ${LoginUrl} - login FE link

        String activationLink = String.format("%s%s?token=%s",
                serverConfigData.isProductDeploy() ? serverConfigData.getProdServer().getBaseUrl()
                        : serverConfigData.getDevServer().getBaseUrl(),
                mailingConfigData.getRegisterEmail().getPath(),
                token);
        String subject = mailingConfigData.getRegisterEmail().getSubject();

        // Load and replace content
        String htmlContent = loadHtmlTemplate("mail/activation.html");
        htmlContent = htmlContent.replace("${ActivationUrl}", activationLink);
        htmlContent = htmlContent.replace("${LoginUrl}", serverConfigData.getFeLoginUrl());

        sendEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendEmail(String to, String subject, String content) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    private String loadHtmlTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        Path filePath = resource.getFile().toPath();
        return Files.readString(filePath, StandardCharsets.UTF_8);
    }
}
