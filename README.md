# nextjs-springboot-mservices

A full-stack, microservice-based project integrating **Next.js** (frontend) with **Spring Boot microservices**, secured via **Keycloak**, documented using **Swagger/OpenAPI**, and orchestrated with **Docker Compose**.

---

## 🏗️ Architecture Overview

```
+--------------------+       +-------------------------+
|  Next.js Frontend  | <---> | Spring Cloud Gateway    |
+--------------------+       +-------------------------+
                                       |
         +-----------------------------+------------------------------+
         |                             |                              |
+-------------------+      +---------------------+      +----------------------+
|  Product Service   |      |  Auth/User Service  |      |  Config Server       |
+-------------------+      +---------------------+      +----------------------+
         |                             |
         +-----------+----------------+
                     |
               +------------+
               |  Eureka    |
               | Discovery  |
               +------------+

+------------------+
|   Keycloak Auth   |
| (Dockerized IAM) |
+------------------+

+------------------+
|     ELK Stack     |
| (Central Logging) |
+------------------+
```

---

## 📦 Module Overview

| Module               | Description                                             |
|----------------------|---------------------------------------------------------|
| `nextjs-frontend`    | Next.js app consuming product APIs                      |
| `api-gateway`        | Routes requests to microservices                        |
| `product-service`    | Spring Boot CRUD service for products                   |
| `auth-service`       | JWT integration via Keycloak                            |
| `eureka-server`      | Service discovery via Spring Cloud Eureka               |
| `config-server`      | Centralized Spring Boot configuration                   |
| `elk`                | Elasticsearch, Logstash, Kibana stack for logging       |
| `docker-compose.yml`| Starts entire stack in dev environment                  |

---

## 🚀 How to Start the Project

### 1. Clone the Repo

```bash
git clone https://github.com/yourusername/nextjs-springboot-mservices.git
cd nextjs-springboot-mservices
```

### 2. Start the Stack

```bash
docker-compose up --build
```

This will spin up:
- Eureka, Gateway, Product & Auth services
- Keycloak (with imported realm)
- Swagger UI at `http://localhost:8080/swagger-ui.html`
- Next.js frontend at `http://localhost:3000`

### 3. Access Keycloak

- URL: `http://localhost:8180`
- Realm: `microservice-realm`
- Admin: `admin / admin`
- Test User: `user1 / pass1`

---

## 🧩 Key Integrations

- ✅ **Next.js + REST APIs**: UI consumes product APIs securely
- ✅ **Spring Cloud Gateway**: Central routing & token relay
- ✅ **Keycloak Integration**: Full OAuth2/JWT support
- ✅ **Liquibase**: Database migrations
- ✅ **Swagger/OpenAPI**: Auto API docs (`/swagger-ui.html`)
- ✅ **ELK Stack**: Centralized distributed logs

---

## 🔧 Build & Deployment Notes

### 📦 Maven Build (for backend services)

```bash
cd product-service
./mvnw clean package
```

### 🐳 Docker Compose

```bash
docker-compose up --build
```

- Add `docker-compose.override.yml` for custom dev overrides
- To scale services, use `--scale product-service=2`

### 🌐 Production Suggestions

- Use PostgreSQL (already configured)
- Configure HTTPS for gateway
- Externalize secrets using Docker secrets or Vault
- CI/CD: GitHub Actions, GitLab CI, or Jenkins pipelines

---

## 📚 Swagger / API Docs

Available at:
```
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

Includes:
- Product CRUD endpoints
- Security scheme (JWT Bearer)

---

## ✅ Goals of the Project

- Demonstrate full-stack microservice design
- Use real-world tools: Swagger, Keycloak, Eureka, Config Server
- Be extensible for deployments to AWS, GCP, Azure
- Serve as a tech portfolio for interviews or PoCs

---

## 📜 License

MIT — use freely for learning, demo, and commercial use with attribution.
