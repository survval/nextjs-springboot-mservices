package com.example.tenantregistry.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * Entity representing a tenant in the system.
 * <p>
 * Each tenant has their own:
 * <ul>
 *   <li>Keycloak realm</li>
 *   <li>Database schema</li>
 *   <li>White-label UI configuration</li>
 * </ul>
 */
@Entity
@Table(name = "tenants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier for the tenant, used for schema names and Keycloak realm IDs.
     * Must be lowercase, alphanumeric with hyphens only.
     */
    @Column(nullable = false, unique = true)
    private String identifier;

    /**
     * Display name for the tenant.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Primary contact email for the tenant.
     */
    @Column(nullable = false)
    private String contactEmail;

    /**
     * Whether the tenant is active.
     */
    @Column(nullable = false)
    private boolean active;

    /**
     * Keycloak realm ID for this tenant.
     */
    @Column(nullable = false)
    private String keycloakRealm;

    /**
     * Database schema name for this tenant.
     */
    @Column(nullable = false)
    private String dbSchema;

    /**
     * Primary color for white-label UI.
     */
    @Column
    private String primaryColor;

    /**
     * Logo URL for white-label UI.
     */
    @Column
    private String logoUrl;

    /**
     * When the tenant was created.
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * When the tenant was last updated.
     */
    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}