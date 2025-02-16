package com.shitcode.demo1.properties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "rsa")
public class RsaKeyConfigData {
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
}