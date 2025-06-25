# Keycloak Integration Service

This module provides Keycloak integration and provisioning for the **nextjs-springboot-mservices** stack.

* **Keycloak Admin Client** — programmatic management of Keycloak realms and users
* **Realm Bootstrap** — automatic provisioning of realms, roles, and test users
* **Admin API** — REST endpoints for Keycloak management operations
* **Basic Auth Config** — configuration for securing admin endpoints

---

## 📂 Module Structure

```
keycloak/
├── pom.xml
├── realm-export.json
├── KeycloakProvisionerApplication.java
├── KeycloakAdminConfig.java
├── KeycloakAdminService.java
├── KeycloakAdminController.java
├── BasicAuthConfig.java
└── RealmBootstrap.java
```

## 🚀 Quick Start (Local)

```bash
# from project root
cd keycloak
mvn spring-boot:run
```

## Features

- **Realm Management**: Create, update, and delete Keycloak realms
- **User Management**: Create, update, and delete users within realms
- **Role Management**: Define and assign roles to users
- **Client Management**: Configure OAuth2/OIDC clients
- **Automatic Bootstrapping**: Initialize Keycloak with predefined configuration on startup

## Configuration

The service can be configured using the following environment variables:

- `KEYCLOAK_URL`: Keycloak server URL (default: http://localhost:8180)
- `KEYCLOAK_ADMIN_USER`: Keycloak admin username (default: admin)
- `KEYCLOAK_ADMIN_PASSWORD`: Keycloak admin password (default: admin)
- `BOOTSTRAP_ENABLED`: Enable/disable automatic bootstrapping (default: true)

## API Endpoints

### Realm Management

- `POST /api/keycloak/realms`: Create a new realm
- `GET /api/keycloak/realms/{realmName}`: Get realm details
- `DELETE /api/keycloak/realms/{realmName}`: Delete a realm

### User Management

- `POST /api/keycloak/realms/{realmName}/users`: Create a new user
- `GET /api/keycloak/realms/{realmName}/users`: List users in a realm
- `GET /api/keycloak/realms/{realmName}/users/{userId}`: Get user details
- `PUT /api/keycloak/realms/{realmName}/users/{userId}`: Update a user
- `DELETE /api/keycloak/realms/{realmName}/users/{userId}`: Delete a user

## Security

The admin API endpoints are secured with Basic Authentication. The credentials can be configured using:

- `ADMIN_USER`: Admin username for the API (default: admin)
- `ADMIN_PASSWORD`: Admin password for the API (default: password)

## Integration with Other Services

This service is used by:

- **API Gateway**: For JWT validation
- **Tenant Registry Service**: For tenant-specific realm creation
- **Product Service**: For user authentication and authorization

## Realm Export/Import

The `realm-export.json` file contains a pre-configured realm that can be imported into Keycloak. This includes:

- Roles: admin, user
- Clients: frontend, api-gateway
- Test users: user1/pass1, admin1/pass1