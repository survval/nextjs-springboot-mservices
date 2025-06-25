package com.example.productservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Data Transfer Object for {@link com.example.productservice.domain.Product}.
 * <p>
 * This is an immutable record used for transferring product data in API responses.
 * Using Java 17 record feature for concise, immutable data transfer objects.
 */
public record ProductDTO(
    Long id,
    String name,
    BigDecimal price,
    String category,
    Instant createdAt
) {}