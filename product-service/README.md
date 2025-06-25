# Product Service

This module provides product management capabilities for the **nextjs-springboot-mservices** stack.

* **RESTful API** — CRUD operations for product management
* **JWT Security** — Secured with Keycloak OAuth2/OIDC
* **OpenAPI Documentation** — Swagger UI for API exploration
* **Database Integration** — JPA/Hibernate with Liquibase migrations

---

## 📂 Module Structure

```
product-service/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/example/productservice/
    │   │   ├── config/
    │   │   │   ├── OpenApiConfig.java
    │   │   │   └── SecurityConfig.java
    │   │   ├── controller/
    │   │   │   └── ProductController.java
    │   │   ├── domain/
    │   │   │   └── Product.java
    │   │   ├── filter/
    │   │   │   └── ProductPredicates.java
    │   │   ├── repo/
    │   │   │   └── ProductRepository.java
    │   │   └── service/
    │   │       └── ProductService.java
    │   └── resources/
    │       ├── application.yml
    │       └── db/changelog/
    │           └── db.changelog-master.yml
    └── test/ # (add your tests here)
```

## 🚀 Quick Start (Local)

```bash
# from project root
cd product-service
mvn spring-boot:run
```

## Features

- **Product Management**: Create, read, update, and delete products
- **Product Search**: Filter products by various criteria
- **Pagination**: Support for paginated results
- **Sorting**: Support for sorting results by different fields
- **Security**: JWT-based authentication and authorization

## API Endpoints

### Product Management

- `POST /api/products`: Create a new product
- `GET /api/products/{id}`: Get a product by ID
- `GET /api/products`: Get all products (with pagination and filtering)
- `PUT /api/products/{id}`: Update a product
- `DELETE /api/products/{id}`: Delete a product

## Security

The API endpoints are secured with OAuth2/OIDC using Keycloak. The following roles are used:

- `ADMIN`: Required for create, update, and delete operations
- `USER`: Required for read operations

## Database Schema

The service uses a relational database with the following schema:

### Product Table

| Column      | Type         | Description                   |
|-------------|--------------|-------------------------------|
| id          | UUID         | Primary key                   |
| name        | VARCHAR(255) | Product name                  |
| description | TEXT         | Product description           |
| price       | DECIMAL      | Product price                 |
| category    | VARCHAR(100) | Product category              |
| created_at  | TIMESTAMP    | Creation timestamp            |
| updated_at  | TIMESTAMP    | Last update timestamp         |

## OpenAPI Documentation

The service provides OpenAPI documentation that can be accessed at:

```
http://localhost:8080/swagger-ui.html
```

When running behind the API Gateway, it can be accessed at:

```
http://localhost:8080/product-service/swagger-ui.html
```

## Configuration

The service can be configured using the following environment variables:

- `DB_HOST`: Database host
- `DB_PORT`: Database port (default: 5432)
- `DB_NAME`: Database name
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `KEYCLOAK_URL`: Keycloak server URL
- `KEYCLOAK_REALM`: Keycloak realm name

## Integration with Other Services

This service integrates with:

- **API Gateway**: For routing and security
- **Eureka Server**: For service discovery
- **Config Server**: For centralized configuration
- **Keycloak**: For authentication and authorization