package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Minimal WebFlux security setup:
 * • Disable CSRF (stateless API)
 * • Let JwtAuthFilter do the heavy lifting
 */
@Configuration
public class GatewaySecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
          .csrf(ServerHttpSecurity.CsrfSpec::disable)   // gateway is stateless
          .authorizeExchange(ex -> ex.anyExchange().permitAll()); // JwtAuthFilter gate-keeps
        return http.build();
    }
}
