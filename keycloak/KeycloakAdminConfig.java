package com.example.keycloak.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakAdminConfig {

  @Value("${kc.server-url}")   String server;
  @Value("${kc.admin-username}") String user;
  @Value("${kc.admin-password}") String pass;

  @Bean
  public Keycloak keycloakAdmin() {
    return KeycloakBuilder.builder()
        .serverUrl(server)
        .realm("master")
        .clientId("admin-cli")
        .username(user)
        .password(pass)
        .build();
  }
}
