package com.example.tenantregistry.dto;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.Instant;

/**
 * Data Transfer Object for {@link com.example.tenantregistry.domain.Tenant}.
 * <p>
 * This is an immutable value object used for transferring tenant data between layers.
 */
@Value
@Builder
@With
public class TenantDTO {
    Long id;
    String identifier;
    String name;
    String contactEmail;
    boolean active;
    String keycloakRealm;
    String dbSchema;
    String primaryColor;
    String logoUrl;
    Instant createdAt;
    Instant updatedAt;
}