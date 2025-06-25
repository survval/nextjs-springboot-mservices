# Spring Cloud Config Server

Central place for externalized configuration across every micro-service in the
**nextjs-springboot-mservices** stack.

* **YAML / properties** — Serves configuration files over HTTP (`/application-{profile}.yml`)
* **Multiple backends** — Supports native folder (dev) or Git (prod) backends
* **Service discovery** — Registers with Eureka (except in unit tests) so clients can locate it
* **Hot reload** — Exposes `/actuator/refresh` so services can hot-reload config without restart

---

## 📂 Module layout

```
config-server/
├── pom.xml
└── src/main/
    ├── java/com/example/config/
    │   └── ConfigServerApplication.java
    └── resources/
        ├── application.yml # common defaults (no secrets)
        ├── application-local.yml # laptop / Docker Desktop
        ├── application-dev.yml # dev compose stack
        ├── application-test.yml # CI / Testcontainers
        └── application-prod.yml # staging / prod / k8s
```

---

## 🚀 Quick start (local)

```bash
# assume ../config-repo directory contains your YAML files
cd config-server
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run
open http://localhost:8888/product-service/local   # sample lookup
```

## Local defaults

| Setting | Fallback value |
|---------|----------------|
| Port    | 8888           |
| Backend | Native folder ../config-repo/ |
| Eureka  | Disabled       |

## 🗺️ Profiles

| Profile | Backend | Eureka | Notes |
|---------|---------|--------|-------|
| local   | Native FS (../config-repo) | Off | Quick IDE run |
| dev     | Native FS (/opt/config-repo) | On | Docker-Compose volume |
| test    | Native FS (/tmp/config-repo) | Off | Fast CI builds |
| prod    | Git (${CONFIG_REPO_URI}) | On | High availability |

## Secrets

Only local has fallback paths. All other profiles rely on environment
variables: `CONFIG_REPO_URI`, `CONFIG_REPO_USER`, `CONFIG_REPO_PASS`,
`EUREKA_URL`, etc.

## Adding a new config file

### Dev / local

```bash
mkdir -p ../config-repo
echo "welcome.message: Hello from Config!" > ../config-repo/gateway-dev.yml
curl -X POST http://localhost:8888/actuator/refresh   # hot reload
```

### Prod

Commit to your Git repo → Config Server auto-pulls on next poll or you
trigger `/actuator/refresh`.

## 🔗 How clients consume config

```yaml
spring:
  config.import: configserver:http://config-server:8888/
```

Spring Cloud will then call:

```
GET /{app-name}/{profile}
```

Example:

```
GET /product-service/dev
```

## Useful endpoints

| URL | Purpose |
|-----|---------|
| `/actuator/health` | Liveness check |
| `/actuator/info` | Git commit ID (if enabled) |
| `/actuator/refresh` | Re-load config without restart |
| `/gateway/dev` | Raw YAML for gateway-dev.yml |

## 🧰 Production checklist

- Run at least 2 replicas behind a load balancer.
- Store encrypted secrets via Vault + Spring Cloud Config (or Key Vault, AWS Secrets Manager).
- Protect `/actuator/refresh` with auth or network policies.
- Pin Git repo to a specific branch (default-label: main).