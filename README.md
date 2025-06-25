
# 🏗️ nextjs-springboot-mservices with Keycloak

This repository shows an end‑to‑end micro‑services skeleton:

* Spring Boot micro‑services (Eureka discovery, API Gateway, Product Service)
* Keycloak for SSO / JWT
* Next.js App Router front‑end
* PostgreSQL & Liquibase migrations
* Centralised logging (ELK)

## Quick start

```bash
docker compose up --build -d
open http://localhost:8088          # Keycloak admin (admin/admin)
open http://localhost:8081/swagger-ui.html   # Swagger for product‑service
```

Login with **testuser / test123** then call `/products`.

See full documentation inside each module.
