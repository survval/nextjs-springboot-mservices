package com.example.tenantregistry.repository;

import com.example.tenantregistry.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for accessing {@link Tenant} entities.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long>, QuerydslPredicateExecutor<Tenant> {

	/**
	 * Find a tenant by its unique identifier.
	 * @param identifier the tenant identifier
	 * @return the tenant, if found
	 */
	Optional<Tenant> findByIdentifier(String identifier);

	/**
	 * Check if a tenant exists with the given identifier.
	 * @param identifier the tenant identifier
	 * @return true if a tenant exists with the given identifier
	 */
	boolean existsByIdentifier(String identifier);

	/**
	 * Find a tenant by its Keycloak realm ID.
	 * @param keycloakRealm the Keycloak realm ID
	 * @return the tenant, if found
	 */
	Optional<Tenant> findByKeycloakRealm(String keycloakRealm);

	/**
	 * Find a tenant by its database schema name.
	 * @param dbSchema the database schema name
	 * @return the tenant, if found
	 */
	Optional<Tenant> findByDbSchema(String dbSchema);

}