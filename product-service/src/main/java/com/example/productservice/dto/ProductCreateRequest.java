package com.example.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Request DTO for creating a new product.
 * <p>
 * This is an immutable record used for product creation requests.
 * Using Java 17 record feature for concise, immutable data transfer objects.
 */
public record ProductCreateRequest(
    @NotBlank(message = "Product name is required")
    String name,
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    BigDecimal price,
    
    String category
) {}