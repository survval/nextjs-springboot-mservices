package com.example.tenantregistry.dto;

import java.time.Instant;

/**
 * Data Transfer Object for {@link com.example.tenantregistry.domain.Tenant}.
 * <p>
 * This is an immutable record used for transferring tenant data between layers. Using
 * Java 17 record feature for concise, immutable data transfer objects.
 */
public record TenantDTO(Long id, String identifier, String name, String contactEmail, boolean active,
		String keycloakRealm, String dbSchema, String primaryColor, String logoUrl, Instant createdAt,
		Instant updatedAt) {
}