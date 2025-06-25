package com.example.events;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Event published when a user is created, updated, or has role changes.
 * <p>
 * This is an immutable record used for Kafka event messages.
 * Using Java 17 record feature for concise, immutable event objects.
 */
public record UserChanged(
    UUID userId,
    String username,
    String email,
    String tenantId,
    Set<String> roles,
    ChangeType changeType,
    Instant timestamp
) {
    /**
     * Type of change that occurred to the user.
     */
    public enum ChangeType {
        CREATED,
        UPDATED,
        ROLE_CHANGED,
        ACTIVATED,
        DEACTIVATED
    }
}