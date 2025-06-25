package com.example.tenantregistry.controller;

import com.example.tenantregistry.dto.CreateTenantRequest;
import com.example.tenantregistry.dto.TenantDTO;
import com.example.tenantregistry.service.TenantService;
import com.querydsl.core.types.Predicate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.tenantregistry.domain.Tenant;

/**
 * REST controller for managing tenants.
 */
@RestController
@RequestMapping("/api/tenants")
@Tag(name = "Tenant Management", description = "APIs for managing tenants")
@RequiredArgsConstructor
public class TenantController {

	private final TenantService tenantService;

	/**
	 * Creates a new tenant.
	 * @param request the tenant creation request
	 * @return the created tenant
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Create a new tenant",
			description = "Creates a new tenant with Keycloak realm and database schema")
	public TenantDTO createTenant(@Valid @RequestBody CreateTenantRequest request) {
		return tenantService.createTenant(request);
	}

	/**
	 * Gets a tenant by ID.
	 * @param id the tenant ID
	 * @return the tenant, if found
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get a tenant by ID", description = "Returns a tenant by its ID")
	public ResponseEntity<TenantDTO> getTenantById(@PathVariable Long id) {
		return tenantService.getTenantById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Gets a tenant by identifier.
	 * @param identifier the tenant identifier
	 * @return the tenant, if found
	 */
	@GetMapping("/by-identifier/{identifier}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get a tenant by identifier", description = "Returns a tenant by its unique identifier")
	public ResponseEntity<TenantDTO> getTenantByIdentifier(@PathVariable String identifier) {
		return tenantService.getTenantByIdentifier(identifier)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Gets all tenants.
	 * @param predicate the predicate to filter tenants
	 * @param pageable pagination information
	 * @return a page of tenants
	 */
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all tenants", description = "Returns a page of tenants with optional filtering")
	public Page<TenantDTO> getTenants(@QuerydslPredicate(root = Tenant.class) Predicate predicate,
			@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
		return predicate != null ? tenantService.getTenants(predicate, pageable)
				: tenantService.getAllTenants(pageable);
	}

	/**
	 * Updates a tenant.
	 * @param id the tenant ID
	 * @param tenantDTO the updated tenant data
	 * @return the updated tenant
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update a tenant", description = "Updates an existing tenant")
	public ResponseEntity<TenantDTO> updateTenant(@PathVariable Long id, @Valid @RequestBody TenantDTO tenantDTO) {
		try {
			return ResponseEntity.ok(tenantService.updateTenant(id, tenantDTO));
		}
		catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Deletes a tenant.
	 * @param id the tenant ID
	 * @return no content if successful, not found if the tenant does not exist
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete a tenant",
			description = "Deletes a tenant, including its Keycloak realm and database schema")
	public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
		try {
			tenantService.deleteTenant(id);
			return ResponseEntity.noContent().build();
		}
		catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Activates a tenant.
	 * @param id the tenant ID
	 * @return the updated tenant
	 */
	@PutMapping("/{id}/activate")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Activate a tenant", description = "Activates a tenant")
	public ResponseEntity<TenantDTO> activateTenant(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(tenantService.activateTenant(id));
		}
		catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Deactivates a tenant.
	 * @param id the tenant ID
	 * @return the updated tenant
	 */
	@PutMapping("/{id}/deactivate")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Deactivate a tenant", description = "Deactivates a tenant")
	public ResponseEntity<TenantDTO> deactivateTenant(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(tenantService.deactivateTenant(id));
		}
		catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

}