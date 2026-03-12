# Denario — Digital Banking Platform

A production-grade microservices banking application built with Spring Boot, Angular, Keycloak, and WebSocket.

## 🏗️ Architecture

```
denario/
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
| Backend | Spring Boot 3.2.3, Spring Cloud Gateway |
| Security | Keycloak 23.0 (OAuth2 / OIDC) |
| Database | PostgreSQL 15 (one DB per service) |
| Messaging | RabbitMQ 3.12 (async between services) |
| Real-time | WebSocket (STOMP) |
| Frontend | Angular 19, Angular Material |
| DevOps | Docker Compose, GitHub Actions |
| Java | 21 (Eclipse Temurin) |

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

## 🐳 Infrastructure Ports

| Service | Internal Port | External Port |
|---------|--------------|---------------|
| account-service | 8081 | 8081 |
| transaction-service | 8082 | 8082 |
| notification-service | 8083 | 8083 |
| api-gateway | 8080 | 8080 |
| Keycloak | 8080 | 8180 |
| PostgreSQL (account) | 5432 | 5435 |
| PostgreSQL (transaction) | 5432 | 5436 |
| PostgreSQL (notification) | 5432 | 5437 |
| RabbitMQ AMQP | 5672 | 5672 |
| RabbitMQ Management UI | 15672 | 15672 |

## 🔐 Default Credentials

- **Keycloak Admin**: http://localhost:8180 → `admin` / `admin`
- **App User**: `user@denario.com` / `password`
- **RabbitMQ**: http://localhost:15672 → `guest` / `guest`

## 📋 API Endpoints (via Gateway at port 8080)

| Service | Method | Endpoint |
|---------|--------|----------|
| Accounts | GET | /api/accounts/me |
| Accounts | GET | /api/accounts/{id} |
| Accounts | POST | /api/accounts |
| Accounts | GET | /api/accounts/balance?iban={iban} |
| Transactions | POST | /api/transactions/transfer |
| Transactions | GET | /api/transactions/history |
| Transactions | GET | /api/transactions/history/{iban} |
| Notifications | WS | /ws/notifications |

## 🏛️ Why "Denario"?

The *denarius* was the standard silver coin of ancient Rome — the backbone of one of history's most sophisticated economies. Denario brings that same spirit of reliability and precision to modern microservices banking.