# Next.js Frontend

This module provides the user interface for the **nextjs-springboot-mservices** stack.

* **Next.js** — React framework with server-side rendering
* **OAuth2/OIDC** — Authentication via Keycloak
* **REST API Integration** — Consumes backend microservices
* **Responsive Design** — Mobile-friendly UI

---

## 📂 Module Structure

```
nextjs-frontend/
├── package.json
├── next.config.js
├── public/
│   ├── favicon.ico
│   └── images/
└── src/
    ├── app/
    │   ├── layout.js
    │   └── page.js
    ├── components/
    │   ├── auth/
    │   ├── layout/
    │   └── products/
    ├── services/
    │   ├── api.js
    │   └── auth.js
    └── styles/
        └── globals.css
```

## 🚀 Quick Start (Local)

```bash
# from project root
cd nextjs-frontend
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

## Features

- **Product Management**: View, create, update, and delete products
- **Authentication**: Login/logout via Keycloak
- **Authorization**: Role-based access control
- **Responsive Design**: Works on desktop and mobile devices

## Configuration

The frontend can be configured using environment variables in `.env.local`:

```
# API Gateway URL
NEXT_PUBLIC_API_URL=http://localhost:8080

# Keycloak Configuration
NEXT_PUBLIC_KEYCLOAK_URL=http://localhost:8180
NEXT_PUBLIC_KEYCLOAK_REALM=microservice-realm
NEXT_PUBLIC_KEYCLOAK_CLIENT_ID=frontend
```

## Authentication Flow

1. User clicks "Login" button
2. User is redirected to Keycloak login page
3. After successful authentication, user is redirected back to the frontend
4. Frontend stores the JWT token and uses it for API calls
5. Token refresh is handled automatically

## Building for Production

```bash
npm run build
npm run start
```

## Docker Build

```bash
docker build -t nextjs-frontend .
docker run -p 3000:3000 \
  -e NEXT_PUBLIC_API_URL=http://api-gateway:8080 \
  -e NEXT_PUBLIC_KEYCLOAK_URL=http://keycloak:8180 \
  -e NEXT_PUBLIC_KEYCLOAK_REALM=microservice-realm \
  -e NEXT_PUBLIC_KEYCLOAK_CLIENT_ID=frontend \
  nextjs-frontend
```

## Integration with Backend Services

The frontend communicates with the backend services through the API Gateway:

- **Product Service**: `/api/products/*` endpoints
- **Tenant Registry Service**: `/api/tenants/*` endpoints
- **Keycloak**: Authentication and user management

## Development Notes

- Use the `useAuth` hook for authentication-related functionality
- Use the `api` service for making authenticated API calls
- Add environment-specific configuration in `.env.local`, `.env.development`, or `.env.production`
