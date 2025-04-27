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
import com.shitcode.demo1.security.JWTAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    private final ClientConfigData clientConfigData;
    private final JwtDecoder jwtDecoder;
    private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final String[] securePaths = { "/swagger-ui.html",
            "/docs/**",
            "/ui-docs/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/",
            "/h2-console/**" };

    public SecurityConfig(ClientConfigData clientConfigData, JwtDecoder jwtDecoder,
            JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.clientConfigData = clientConfigData;
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Roles
        String adminRole = clientConfigData.getRoles().getAdmin();

        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
                .cors(cors -> cors.disable()) // README: For quick project, if you're deploy this to production
                                              // enviroment
                                              // Please impl cors
                .authorizeHttpRequests(auth -> {
                    auth
                            .anyRequest().permitAll()
                            // Admin paths
                            .requestMatchers(securePaths)
                            .hasRole(adminRole);
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