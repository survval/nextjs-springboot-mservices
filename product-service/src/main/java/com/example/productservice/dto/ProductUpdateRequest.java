package com.example.productservice.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Request DTO for updating an existing product.
 * <p>
 * This is an immutable record used for product update requests.
 * All fields are optional to allow partial updates.
 * Using Java 17 record feature for concise, immutable data transfer objects.
 */
public record ProductUpdateRequest(
    String name,
    
    @Positive(message = "Price must be positive if provided")
    BigDecimal price,
    
    String category
) {}