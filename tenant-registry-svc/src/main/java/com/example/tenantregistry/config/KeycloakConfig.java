package com.example.tenantregistry.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Keycloak admin client.
 * <p>
 * This configuration creates a Keycloak admin client bean that can be used to
 * manage Keycloak realms, clients, roles, and users.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin-username:${KC_ADMIN_USERNAME:?}}")
    private String adminUsername;

    @Value("${keycloak.admin-password:${KC_ADMIN_PASSWORD:?}}")
    private String adminPassword;

    /**
     * Creates a Keycloak admin client bean.
     * <p>
     * This client connects to the master realm with the admin-cli client and
     * uses the provided admin credentials.
     *
     * @return the Keycloak admin client
     */
    @Bean
    public Keycloak keycloakAdminClient() {
        log.info("Initializing Keycloak admin client for server: {}", serverUrl);
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .clientId("admin-cli")
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }
}