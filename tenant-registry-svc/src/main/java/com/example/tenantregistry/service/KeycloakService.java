package com.example.tenantregistry.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing Keycloak realms, clients, roles, and users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.default-client-id:product-management}")
    private String defaultClientId;

    @Value("${keycloak.default-client-secret:${KC_CLIENT_SECRET:?}}")
    private String defaultClientSecret;

    @Value("${keycloak.default-admin-role:tenant-admin}")
    private String defaultAdminRole;

    /**
     * Creates a new realm for a tenant.
     *
     * @param realmId the ID of the realm to create
     * @param displayName the display name of the realm
     * @return true if the realm was created, false if it already existed
     */
    public boolean createRealm(String realmId, String displayName) {
        log.info("Creating realm: {}", realmId);
        
        // Check if realm already exists
        if (realmExists(realmId)) {
            log.info("Realm already exists: {}", realmId);
            return false;
        }
        
        // Create realm
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(realmId);
        realm.setDisplayName(displayName);
        realm.setEnabled(true);
        realm.setRegistrationAllowed(false);
        realm.setRegistrationEmailAsUsername(true);
        
        keycloak.realms().create(realm);
        log.info("Realm created: {}", realmId);
        
        // Create default client
        createClient(realmId, defaultClientId, defaultClientSecret);
        
        // Create default admin role
        createRole(realmId, defaultAdminRole);
        
        return true;
    }
    
    /**
     * Checks if a realm exists.
     *
     * @param realmId the ID of the realm to check
     * @return true if the realm exists
     */
    public boolean realmExists(String realmId) {
        return keycloak.realms().findAll().stream()
                .anyMatch(r -> r.getRealm().equals(realmId));
    }
    
    /**
     * Creates a new client in a realm.
     *
     * @param realmId the ID of the realm
     * @param clientId the ID of the client to create
     * @param clientSecret the secret for the client
     * @return the ID of the created client, or empty if it already existed
     */
    public Optional<String> createClient(String realmId, String clientId, String clientSecret) {
        log.info("Creating client: {} in realm: {}", clientId, realmId);
        
        var realm = keycloak.realm(realmId);
        
        // Check if client already exists
        if (realm.clients().findByClientId(clientId).stream().findFirst().isPresent()) {
            log.info("Client already exists: {} in realm: {}", clientId, realmId);
            return Optional.empty();
        }
        
        // Create client
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(clientId);
        client.setSecret(clientSecret);
        client.setEnabled(true);
        client.setDirectAccessGrantsEnabled(true);
        client.setStandardFlowEnabled(true);
        client.setImplicitFlowEnabled(false);
        client.setServiceAccountsEnabled(true);
        client.setPublicClient(false);
        client.setBearerOnly(false);
        
        Response response = realm.clients().create(client);
        String id = CreatedResponseUtil.getCreatedId(response);
        log.info("Client created: {} in realm: {} with ID: {}", clientId, realmId, id);
        
        return Optional.of(id);
    }
    
    /**
     * Creates a new role in a realm.
     *
     * @param realmId the ID of the realm
     * @param roleName the name of the role to create
     * @return true if the role was created, false if it already existed
     */
    public boolean createRole(String realmId, String roleName) {
        log.info("Creating role: {} in realm: {}", roleName, realmId);
        
        var realm = keycloak.realm(realmId);
        
        // Check if role already exists
        if (realm.roles().list().stream().anyMatch(r -> r.getName().equals(roleName))) {
            log.info("Role already exists: {} in realm: {}", roleName, realmId);
            return false;
        }
        
        // Create role
        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleName);
        role.setDescription("Auto-generated role for " + realmId);
        role.setComposite(false);
        
        realm.roles().create(role);
        log.info("Role created: {} in realm: {}", roleName, realmId);
        
        return true;
    }
    
    /**
     * Creates a new admin user in a realm.
     *
     * @param realmId the ID of the realm
     * @param username the username of the admin user
     * @param email the email of the admin user
     * @param password the password of the admin user
     * @param firstName the first name of the admin user
     * @param lastName the last name of the admin user
     * @return the ID of the created user, or empty if it already existed
     */
    public Optional<String> createAdminUser(String realmId, String username, String email, 
                                           String password, String firstName, String lastName) {
        log.info("Creating admin user: {} in realm: {}", username, realmId);
        
        var realm = keycloak.realm(realmId);
        
        // Check if user already exists
        if (realm.users().search(username).stream().findFirst().isPresent()) {
            log.info("User already exists: {} in realm: {}", username, realmId);
            return Optional.empty();
        }
        
        // Create user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);
        
        Response response = realm.users().create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);
        
        // Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        realm.users().get(userId).resetPassword(credential);
        
        // Assign admin role
        RoleRepresentation adminRole = realm.roles().get(defaultAdminRole).toRepresentation();
        realm.users().get(userId).roles().realmLevel().add(List.of(adminRole));
        
        log.info("Admin user created: {} in realm: {} with ID: {}", username, realmId, userId);
        
        return Optional.of(userId);
    }
    
    /**
     * Deletes a realm.
     *
     * @param realmId the ID of the realm to delete
     * @return true if the realm was deleted, false if it didn't exist
     */
    public boolean deleteRealm(String realmId) {
        log.info("Deleting realm: {}", realmId);
        
        if (!realmExists(realmId)) {
            log.info("Realm doesn't exist: {}", realmId);
            return false;
        }
        
        keycloak.realm(realmId).remove();
        log.info("Realm deleted: {}", realmId);
        
        return true;
    }
}