# API Gateway (Spring Cloud Gateway + Keycloak)

This module is the single entry-point to the micro-services ecosystem:

* **Spring Cloud Gateway** — reactive, non-blocking routing layer
* **Eureka client** — discovers downstream services at runtime
* **JWT validation** — uses Keycloak's JWK set to verify tokens
* **Environment profiles** — `local`, `dev`, `test`, `prod`
* **Swagger-friendly** — passes "Bearer …​" tokens unchanged

---

## 📂 Module structure

```
api-gateway/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/example/gateway/
    │   │   ├── ApiGatewayApplication.java
    │   │   ├── config/GatewaySecurityConfig.java
    │   │   └── filter/JwtAuthFilter.java
    │   └── resources/
    │       ├── application.yml # common defaults
    │       ├── application-local.yml # laptop / Docker Desktop
    │       ├── application-dev.yml # dev compose
    │       ├── application-test.yml # CI / Testcontainers
    │       └── application-prod.yml # staging / prod
    └── test/… # (add your tests here)
```

## 🚀 Quick start (local)

```bash
# from project root
cd api-gateway
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run
```

## Features

- **Centralized Routing**: Routes requests to appropriate microservices
- **Service Discovery**: Automatically discovers services via Eureka
- **Security**: JWT validation and token relay
- **Load Balancing**: Client-side load balancing for multiple service instances
- **Circuit Breaking**: Prevents cascading failures with circuit breakers

## Route Configuration

Routes are configured in the application.yml file:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - RewritePath=/api/(?<segment>.*), /$\{segment}
            
        - id: tenant-registry-service
          uri: lb://tenant-registry-service
          predicates:
            - Path=/api/tenants/**
          filters:
            - RewritePath=/api/(?<segment>.*), /$\{segment}
```

## Security Configuration

The API Gateway validates JWT tokens issued by Keycloak:

```java
// JwtAuthFilter.java
public class JwtAuthFilter implements GatewayFilter, Ordered {
    // JWT validation logic
}

// GatewaySecurityConfig.java
public class GatewaySecurityConfig {
    // Security configuration
}
```

## Environment Variables

The gateway can be configured using the following environment variables:

- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE`: URL of the Eureka server
- `KEYCLOAK_URL`: Keycloak server URL
- `KEYCLOAK_REALM`: Keycloak realm name
- `SERVER_PORT`: Port to run the gateway on (default: 8080)

## Integration with Other Services

The API Gateway integrates with:

- **Eureka Server**: For service discovery
- **Config Server**: For centralized configuration
- **Keycloak**: For JWT validation
- **All Microservices**: For routing requests

## Monitoring and Management

The gateway exposes several endpoints for monitoring and management:

- `/actuator/health`: Health check endpoint
- `/actuator/info`: Information about the gateway
- `/actuator/gateway/routes`: List of configured routes
- `/actuator/gateway/globalfilters`: List of global filters
- `/actuator/gateway/routefilters`: List of route filters

## Production Considerations

For production deployments:

- Run multiple instances behind a load balancer
- Configure HTTPS with proper certificates
- Set up rate limiting to prevent abuse
- Configure proper logging and monitoring
- Use a proper secret management solution for sensitive configuration