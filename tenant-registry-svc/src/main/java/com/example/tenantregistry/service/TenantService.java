package com.example.tenantregistry.service;

import com.example.tenantregistry.domain.Tenant;
import com.example.tenantregistry.dto.CreateTenantRequest;
import com.example.tenantregistry.dto.TenantDTO;
import com.example.tenantregistry.mapper.TenantMapper;
import com.example.tenantregistry.repository.TenantRepository;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for managing tenants.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

	private final TenantRepository tenantRepository;

	private final TenantMapper tenantMapper;

	private final KeycloakService keycloakService;

	private final LiquibaseService liquibaseService;

	/**
	 * Creates a new tenant.
	 * @param request the tenant creation request
	 * @return the created tenant
	 * @throws IllegalArgumentException if a tenant with the same identifier already
	 * exists
	 */
	@Transactional
	public TenantDTO createTenant(CreateTenantRequest request) {
		log.info("Creating tenant with identifier: {}", request.identifier());

		// Check if tenant already exists
		if (tenantRepository.existsByIdentifier(request.identifier())) {
			throw new IllegalArgumentException("Tenant with identifier " + request.identifier() + " already exists");
		}

		// Create Keycloak realm
		String realmId = request.identifier();
		boolean realmCreated = keycloakService.createRealm(realmId, request.name());
		if (!realmCreated) {
			log.warn("Keycloak realm already exists for tenant: {}", request.identifier());
		}

		// Create database schema
		String schemaName = request.identifier().replace("-", "_");
		boolean schemaCreated = liquibaseService.createSchema(schemaName);
		if (!schemaCreated) {
			throw new RuntimeException("Failed to create database schema for tenant: " + request.identifier());
		}

		// Create tenant entity
		Tenant tenant = Tenant.builder()
			.identifier(request.identifier())
			.name(request.name())
			.contactEmail(request.contactEmail())
			.active(true)
			.keycloakRealm(realmId)
			.dbSchema(schemaName)
			.primaryColor(request.primaryColor())
			.logoUrl(request.logoUrl())
			.build();

		tenant = tenantRepository.save(tenant);
		log.info("Tenant created with ID: {}", tenant.getId());

		return tenantMapper.toDto(tenant);
	}

	/**
	 * Gets a tenant by ID.
	 * @param id the tenant ID
	 * @return the tenant, if found
	 */
	@Transactional(readOnly = true)
	public Optional<TenantDTO> getTenantById(Long id) {
		return tenantRepository.findById(id).map(tenantMapper::toDto);
	}

	/**
	 * Gets a tenant by identifier.
	 * @param identifier the tenant identifier
	 * @return the tenant, if found
	 */
	@Transactional(readOnly = true)
	public Optional<TenantDTO> getTenantByIdentifier(String identifier) {
		return tenantRepository.findByIdentifier(identifier).map(tenantMapper::toDto);
	}

	/**
	 * Gets all tenants.
	 * @param pageable pagination information
	 * @return a page of tenants
	 */
	@Transactional(readOnly = true)
	public Page<TenantDTO> getAllTenants(Pageable pageable) {
		return tenantRepository.findAll(pageable).map(tenantMapper::toDto);
	}

	/**
	 * Gets tenants by predicate.
	 * @param predicate the predicate to filter tenants
	 * @param pageable pagination information
	 * @return a page of tenants
	 */
	@Transactional(readOnly = true)
	public Page<TenantDTO> getTenants(Predicate predicate, Pageable pageable) {
		return tenantRepository.findAll(predicate, pageable).map(tenantMapper::toDto);
	}

	/**
	 * Updates a tenant.
	 * @param id the tenant ID
	 * @param tenantDTO the updated tenant data
	 * @return the updated tenant
	 * @throws IllegalArgumentException if the tenant does not exist
	 */
	@Transactional
	public TenantDTO updateTenant(Long id, TenantDTO tenantDTO) {
		log.info("Updating tenant with ID: {}", id);

		Tenant tenant = tenantRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Tenant not found with ID: " + id));

		tenantMapper.updateEntityFromDto(tenantDTO, tenant);
		tenant = tenantRepository.save(tenant);

		log.info("Tenant updated with ID: {}", tenant.getId());

		return tenantMapper.toDto(tenant);
	}

	/**
	 * Deletes a tenant.
	 * @param id the tenant ID
	 * @throws IllegalArgumentException if the tenant does not exist
	 */
	@Transactional
	public void deleteTenant(Long id) {
		log.info("Deleting tenant with ID: {}", id);

		Tenant tenant = tenantRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Tenant not found with ID: " + id));

		// Delete Keycloak realm
		boolean realmDeleted = keycloakService.deleteRealm(tenant.getKeycloakRealm());
		if (!realmDeleted) {
			log.warn("Failed to delete Keycloak realm for tenant: {}", tenant.getIdentifier());
		}

		// Delete database schema
		boolean schemaDropped = liquibaseService.dropSchema(tenant.getDbSchema());
		if (!schemaDropped) {
			log.warn("Failed to drop database schema for tenant: {}", tenant.getIdentifier());
		}

		tenantRepository.delete(tenant);
		log.info("Tenant deleted with ID: {}", id);
	}

	/**
	 * Activates a tenant.
	 * @param id the tenant ID
	 * @return the updated tenant
	 * @throws IllegalArgumentException if the tenant does not exist
	 */
	@Transactional
	public TenantDTO activateTenant(Long id) {
		log.info("Activating tenant with ID: {}", id);

		Tenant tenant = tenantRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Tenant not found with ID: " + id));

		tenant.setActive(true);
		tenant = tenantRepository.save(tenant);

		log.info("Tenant activated with ID: {}", tenant.getId());

		return tenantMapper.toDto(tenant);
	}

	/**
	 * Deactivates a tenant.
	 * @param id the tenant ID
	 * @return the updated tenant
	 * @throws IllegalArgumentException if the tenant does not exist
	 */
	@Transactional
	public TenantDTO deactivateTenant(Long id) {
		log.info("Deactivating tenant with ID: {}", id);

		Tenant tenant = tenantRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Tenant not found with ID: " + id));

		tenant.setActive(false);
		tenant = tenantRepository.save(tenant);

		log.info("Tenant deactivated with ID: {}", tenant.getId());

		return tenantMapper.toDto(tenant);
	}

}
