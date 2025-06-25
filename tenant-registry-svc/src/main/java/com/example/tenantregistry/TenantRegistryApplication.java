package com.example.tenantregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main application class for the Tenant Registry Service.
 * <p>
 * This service is responsible for:
 * <ul>
 * <li>Provisioning new tenants</li>
 * <li>Creating Keycloak realms for each tenant</li>
 * <li>Creating database schemas for each tenant</li>
 * <li>Managing tenant metadata</li>
 * </ul>
 */
@SpringBootApplication
@EnableJpaRepositories
public class TenantRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenantRegistryApplication.class, args);
	}

}