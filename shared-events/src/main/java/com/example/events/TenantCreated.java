package com.example.events;

import java.time.Instant;

/**
 * Event published when a new tenant is created.
 * <p>
 * This is an immutable record used for Kafka event messages.
 * Using Java 17 record feature for concise, immutable event objects.
 */
public record TenantCreated(
    String identifier,
    String name,
    String contactEmail,
    String keycloakRealm,
    String dbSchema,
    Instant createdAt
) {}