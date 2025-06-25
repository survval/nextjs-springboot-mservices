package com.example.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a product is deleted.
 * <p>
 * This is an immutable record used for Kafka event messages.
 * Using Java 17 record feature for concise, immutable event objects.
 * This serves as a tombstone event for audit purposes.
 */
public record ProductDeleted(
    UUID productId,
    String tenantId,
    String deletedBy,
    Instant deletedAt
) {}