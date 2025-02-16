package com.shitcode.demo1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.shitcode.demo1.properties.RsaKeyConfigData;

@Configuration
@Primary
public class JwtConfig {

    private final RsaKeyConfigData rsaKeyConfigData;

    public JwtConfig(RsaKeyConfigData rsaKeyConfigData) {
        this.rsaKeyConfigData = rsaKeyConfigData;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeyConfigData.getPublicKey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new com.nimbusds.jose.jwk.RSAKey.Builder(rsaKeyConfigData.getPublicKey())
                .privateKey(rsaKeyConfigData.getPrivateKey())
                .build();

        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}