# Tenant Registry Service

The Tenant Registry Service is responsible for provisioning and managing tenants in the multi-tenant Product Management platform.

## Features

- Create, read, update, and delete tenants
- Provision Keycloak realms for each tenant
- Create database schemas for each tenant
- Manage tenant metadata (name, contact info, white-label UI settings)

## Architecture

This service is part of a microservices architecture and is responsible for tenant management. It:

1. Stores tenant metadata in its own database
2. Creates and manages Keycloak realms for tenant authentication and authorization
3. Creates and manages database schemas for tenant data isolation
4. Exposes REST APIs for tenant management

## Technologies

- Java 17
- Spring Boot 3.5.x
- Spring Data JPA
- QueryDSL
- Liquibase
- PostgreSQL
- Keycloak Admin Client
- MapStruct
- Lombok

## Configuration

The service uses Spring profiles for different environments:

- `local`: Local development configuration
- `dev`: Development environment configuration
- `test`: Test environment configuration
- `prod`: Production environment configuration

### Environment Variables

#### Database Configuration
- `DB_HOST`: Database host
- `DB_PORT`: Database port (default: 5432)
- `DB_NAME`: Database name
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password

#### Keycloak Configuration
- `KEYCLOAK_URL`: Keycloak server URL
- `KEYCLOAK_REALM`: Keycloak realm for this service
- `KC_ADMIN_USERNAME`: Keycloak admin username
- `KC_ADMIN_PASSWORD`: Keycloak admin password
- `KC_CLIENT_SECRET`: Default client secret for tenant realms

## API Endpoints

### Tenant Management

- `POST /api/tenants`: Create a new tenant
- `GET /api/tenants/{id}`: Get a tenant by ID
- `GET /api/tenants/by-identifier/{identifier}`: Get a tenant by identifier
- `GET /api/tenants`: Get all tenants (with pagination and filtering)
- `PUT /api/tenants/{id}`: Update a tenant
- `DELETE /api/tenants/{id}`: Delete a tenant
- `PUT /api/tenants/{id}/activate`: Activate a tenant
- `PUT /api/tenants/{id}/deactivate`: Deactivate a tenant

## Security

All API endpoints are secured with OAuth2/OIDC using Keycloak. The following roles are required:

- `ADMIN`: Required for all tenant management operations

## Building and Running

### Prerequisites

- Java 17 or later
- Maven 3.8 or later
- PostgreSQL 15
- Keycloak 23

### Building

```bash
mvn clean package
```

### Running Locally

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Running with Docker

```bash
docker build -t tenant-registry-svc .
docker run -p 8081:8081 \
  -e DB_HOST=postgres \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  -e KEYCLOAK_URL=http://keycloak:8080 \
  -e KC_ADMIN_USERNAME=admin \
  -e KC_ADMIN_PASSWORD=admin \
  -e KC_CLIENT_SECRET=secret \
  tenant-registry-svc
```