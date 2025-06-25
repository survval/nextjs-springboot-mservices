package com.example.tenantregistry.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Central configuration for all MapStruct mappers.
 * <p>
 * This configuration can be shared across all mappers to ensure consistent behavior.
 */
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapperConfig {
}