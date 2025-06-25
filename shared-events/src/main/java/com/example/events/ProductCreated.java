package com.example.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a new product is created.
 * <p>
 * This is an immutable record used for Kafka event messages.
 * Using Java 17 record feature for concise, immutable event objects.
 */
public record ProductCreated(
    UUID productId,
    String name,
    BigDecimal price,
    String category,
    String tenantId,
    Instant createdAt
) {}