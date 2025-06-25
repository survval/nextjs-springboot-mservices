package com.example.tenantregistry.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for creating a new tenant.
 * <p>
 * This is an immutable record used for tenant creation requests. Using Java 17 record
 * feature for concise, immutable data transfer objects.
 */
public record CreateTenantRequest(
		/**
		 * Unique identifier for the tenant, used for schema names and Keycloak realm IDs.
		 * Must be lowercase, alphanumeric with hyphens only.
		 */
		@NotBlank(message = "Identifier is required") @Pattern(regexp = "^[a-z0-9-]+$",
				message = "Identifier must contain only lowercase letters, numbers, and hyphens") String identifier,

		/**
		 * Display name for the tenant.
		 */
		@NotBlank(message = "Name is required") String name,

		/**
		 * Primary contact email for the tenant.
		 */
		@NotBlank(message = "Contact email is required") @Email(
				message = "Contact email must be a valid email address") String contactEmail,

		/**
		 * Primary color for white-label UI (optional).
		 */
		@Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
				message = "Primary color must be a valid hex color code (e.g., #FF5733)",
				flags = Pattern.Flag.CASE_INSENSITIVE) String primaryColor,

		/**
		 * Logo URL for white-label UI (optional).
		 */
		String logoUrl) {
}