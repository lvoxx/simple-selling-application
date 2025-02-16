package com.shitcode.demo1.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.google.errorprone.annotations.DoNotMock;
import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.properties.LvoxxServerConfigData;
import com.shitcode.demo1.service.MailService;
import com.shitcode.demo1.utils.LoggingModel;

@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
@DoNotMock
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final LvoxxServerConfigData serverConfigData;

    public MailServiceImpl(JavaMailSender mailSender, LvoxxServerConfigData serverConfigData) {
        this.mailSender = mailSender;
        this.serverConfigData = serverConfigData;
    }

    @Override
    public void sendActivationEmail(String toEmail, String token, String username) {
        String activationLink = String.format("%s%s?token=%s",
                serverConfigData.isProductDeploy() ? serverConfigData.getProdServer().getBaseUrl()
                        : serverConfigData.getDevServer().getBaseUrl(),
                "{mailing.register-email.path}",
                token);

        String subject = "{mailing.register-email.subject}";
        String content = String.format(
                "{mailing.register-email.content}",
                username,
                activationLink);

        sendEmail(toEmail, subject, content);
    }

    @Override
    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

}
