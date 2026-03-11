# FinFlow — Digital Banking Platform

A production-grade microservices banking application built with Spring Boot, Angular, Keycloak, and WebSocket.

## 🏗️ Architecture

```
finflow/
├── account-service/        # Port 8081 — Account & balance management
├── transaction-service/    # Port 8082 — Transfers & transaction history
├── notification-service/   # Port 8083 — Real-time alerts via WebSocket
├── api-gateway/            # Port 8080 — Single entry point (Spring Cloud Gateway)
├── frontend-angular/       # Port 4200 — Angular SPA
├── infrastructure/
│   ├── keycloak/           # Auth server config (OAuth2/OIDC)
│   └── postgres/           # DB init scripts
└── .github/workflows/      # CI/CD with GitHub Actions
```

## 🔧 Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.x, Spring Cloud Gateway |
| Security | Keycloak (OAuth2 / OIDC) |
| Database | PostgreSQL (one DB per service) |
| Messaging | RabbitMQ (async between services) |
| Real-time | WebSocket (STOMP) |
| Frontend | Angular 17, Angular Material |
| DevOps | Docker Compose, GitHub Actions |

## 🚀 Quick Start

```bash
# 1. Start all infrastructure (Keycloak + PostgreSQL + RabbitMQ)
docker compose up -d

# 2. Wait for Keycloak to be ready (~30 seconds)
# Then open: http://localhost:8180/admin  (admin/admin)

# 3. Start services (in separate terminals or via IDE)
cd account-service && ./mvnw spring-boot:run
cd transaction-service && ./mvnw spring-boot:run
cd notification-service && ./mvnw spring-boot:run
cd api-gateway && ./mvnw spring-boot:run

# 4. Start Angular frontend
cd frontend-angular && npm install && ng serve
```

## 🔐 Default Credentials

- **Keycloak Admin**: http://localhost:8180 → admin / admin
- **App User**: user@finflow.com / password
- **RabbitMQ**: http://localhost:15672 → guest / guest

## 📋 API Endpoints (via Gateway at port 8080)

| Service | Endpoint |
|---------|----------|
| Accounts | GET /api/accounts/me |
| Accounts | GET /api/accounts/{id}/balance |
| Transactions | POST /api/transactions/transfer |
| Transactions | GET /api/transactions/history |
| Notifications | WS /ws/notifications |
