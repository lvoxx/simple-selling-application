package com.shitcode.demo1.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.shitcode.demo1.properties.ClientConfigData;
import com.shitcode.demo1.properties.SecurityPathsConfigData;
import com.shitcode.demo1.security.JWTAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityPathsConfigData securityPathsConfigData;
    private final ClientConfigData clientConfigData;
    private final JwtDecoder jwtDecoder;
    private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(SecurityPathsConfigData securityPathsConfigData, ClientConfigData clientConfigData,
            JwtDecoder jwtDecoder, JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.securityPathsConfigData = securityPathsConfigData;
        this.clientConfigData = clientConfigData;
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
                .cors(cors -> cors.disable()) // For quick project, if you're deploy this to production enviroment
                                              // Please impl cors
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(securityPathsConfigData.getEveryone()).permitAll()
                            // Users paths
                            .requestMatchers(securityPathsConfigData.getUser())
                            .hasRole(clientConfigData.getRoles().getUser())
                            // Super user paths
                            .requestMatchers(securityPathsConfigData.getSuperUser())
                            .hasRole(clientConfigData.getRoles().getSuperUser())
                            // Admin paths
                            .requestMatchers(securityPathsConfigData.getAdmin())
                            .hasRole(clientConfigData.getRoles().getAdmin())
                            .anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                }) // * Stateless API
                .oauth2ResourceServer((oauth2) -> oauth2.jwt((jwt) -> {
                    jwt.decoder(jwtDecoder);
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter());
                }))
                // .httpBasic(Customizer.withDefaults()) // * Use Basic Auth
                .build();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            @Qualifier("springUserDetailsService") UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder)
            throws Exception {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Map "scope" or "scp" claim to authorities, and prefix with "ROLE_"
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope"); // Use "scp" if your token uses that

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}