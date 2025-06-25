# Eureka Server

Service-discovery hub for the **nextjs-springboot-mservices** stack.  
All micro-services register their host/port here so the API Gateway (and each
other) can discover them at runtime, enabling zero hard-coded URLs.

---

## 📂 Module layout

```
eureka-server/
├── pom.xml
└── src/main/
    ├── java/com/example/eureka/
    │   └── EurekaServerApplication.java
    └── resources/
        ├── application.yml # defaults (no secrets)
        ├── application-local.yml # laptop / Docker Desktop
        ├── application-dev.yml # dev docker-compose stack
        ├── application-test.yml # CI / Testcontainers
        └── application-prod.yml # staging / prod / k8s
```

---

## 🚀 Quick start (local laptop)

```bash
cd eureka-server
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run
open http://localhost:8761      # Eureka dashboard
```

## 🗺️ Profiles

| Profile | Purpose | Behaviour |
|---------|---------|-----------|
| local | Single-node dev on laptop | No registry replication; dashboard at localhost:8761. |
| dev | Docker-Compose dev stack | Container host name eureka-server; other containers reach it at that DNS. |
| test | CI / Testcontainers | Random port, no registration required (fast unit tests). |
| prod | Staging / prod / k8s | Exposes port 8761; other Eureka nodes or clients connect via DNS/Service. |

## Secrets

The YAML files contain no passwords—if you later secure the REST API
(basic auth or mTLS), inject creds via environment variables and reference
them as ${EUREKA_USER} / ${EUREKA_PASS}.

## Health & monitoring

- GET `/actuator/health` – standard Spring Boot health check
- GET `/actuator/info` – build info (requires Actuator in pom.xml)
- `/metrics`, `/prometheus` – add `management.endpoints.web.exposure.include`
  in application-prod.yml if you scrape with Prometheus.

## Clustering tips

- **Multiple nodes**: run 2–3 replicas with EUREKA_DEFAULT_ZONE
  pointing at each other.
- **Sticky DNS**: In Kubernetes, expose Eureka via a headless service
  so nodes can register by hostname.
- **Lease renewal**: Downstream services send heartbeats every 30 seconds by
  default. For prod, set `eureka.instance.leaseRenewalIntervalInSeconds=10`
  to detect failures faster.

## Removing zombie instances

If you kill services abruptly while running locally, they linger as DOWN
until the TTL (default 90 s) expires.
- **Option A**: wait
- **Option B**: click "status = DOWN" → "delete" in the UI.