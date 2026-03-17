# Denario — Digital Banking Platform

A production-grade microservices banking platform built with Spring Boot 3, Angular 19, Keycloak, RabbitMQ, and WebSocket. Named after the *denarius* — the silver coin that powered Rome's economy — Denario brings the same reliability and precision to modern distributed systems.

---

## Architecture

```
                         ┌─────────────────────────────────────────┐
                         │           Angular 19 SPA                │
                         │         localhost:4200                   │
                         └──────────────────┬──────────────────────┘
                                            │ HTTP / WebSocket
                         ┌──────────────────▼──────────────────────┐
                         │            API Gateway                  │
                         │  Spring Cloud Gateway  :8080            │
                         │  JWT auth · Circuit breaker · CORS      │
                         └────┬───────────┬──────────────┬─────────┘
                              │           │              │
               ┌──────────────▼──┐  ┌─────▼──────┐  ┌──▼──────────────┐
               │ account-service │  │transaction │  │notification     │
               │    :8081        │  │ service    │  │service  :8083   │
               │                 │  │  :8082     │  │WebSocket STOMP  │
               └────────┬────────┘  └─────┬──────┘  └──────┬──────────┘
                        │                 │                 │
               ┌────────▼─────────────────▼─────────────────▼──────────┐
               │                    RabbitMQ                            │
               │   account.exchange      transaction.exchange           │
               └────────────────────────────────────────────────────────┘
               ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐
               │ PostgreSQL   │  │ PostgreSQL   │  │   PostgreSQL     │
               │ account_db   │  │transaction_db│  │notification_db   │
               │  :5435       │  │  :5436       │  │  :5437           │
               └──────────────┘  └──────────────┘  └──────────────────┘
```

### Service responsibilities

| Service | Port | Responsibility |
|---------|------|---------------|
| `api-gateway` | 8080 | Single entry point — routing, JWT validation, circuit breakers, CORS |
| `account-service` | 8081 | Account lifecycle, IBAN generation, balance management |
| `transaction-service` | 8082 | Fund transfers, transaction history |
| `notification-service` | 8083 | Real-time alerts via WebSocket STOMP + persistent history |

### Communication patterns

| Pattern | Used for | Why |
|---------|----------|-----|
| Synchronous HTTP (WebClient) | Balance check before transfer | Must block — transfer cannot proceed without knowing the balance |
| Async RabbitMQ events | Account created → notify, Transaction completed → notify | Temporal decoupling — notifications are not in the critical payment path |
| WebSocket STOMP | Server → client real-time push | Live notification delivery without polling |

> See [`docs/adr/`](docs/adr/) for the architectural decisions behind these choices.

---

## Project Structure

```
denario/
├── services/                    # Backend microservices (Java 21 / Spring Boot 3.2)
│   ├── api-gateway/
│   ├── account-service/
│   ├── transaction-service/
│   └── notification-service/
│
├── frontend/
│   └── web/                     # Angular 19 SPA
│
├── infrastructure/
│   ├── docker/
│   │   └── docker-compose.yml   # Full local dev stack
│   ├── keycloak/
│   │   └── denario-realm.json   # Realm, users, roles (auto-imported)
│   └── postgres/
│       └── *.sql                # DB init scripts
│
├── docs/
│   └── adr/                     # Architecture Decision Records
│       ├── 001-microservices-over-monolith.md
│       ├── 002-keycloak-for-authentication.md
│       └── 003-rabbitmq-for-async-events.md
│
├── scripts/
│   └── start-local.sh
│
├── .github/
│   ├── workflows/
│   │   ├── ci-services.yml      # Backend CI (triggered on services/**)
│   │   └── ci-frontend.yml      # Frontend CI (triggered on frontend/**)
│   ├── CODEOWNERS
│   └── pull_request_template.md
│
└── Makefile                     # Developer convenience commands
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2.3, Java 21 (Eclipse Temurin) |
| API Gateway | Spring Cloud Gateway 2023.0.1 |
| Security | Keycloak 23.0 — OAuth2 / OIDC + JWT |
| Database | PostgreSQL 15 — one isolated DB per service |
| Migrations | Flyway |
| Messaging | RabbitMQ 3.12 — topic exchanges |
| Real-time | WebSocket STOMP (SimpMessagingTemplate) |
| Frontend | Angular 19, Angular Material, Signals |
| Build | Maven 3 (wrapper per service) |
| DevOps | Docker Compose, GitHub Actions |

---

## Quick Start

### Prerequisites

- Docker Desktop
- Java 21
- Node.js 20+ (for frontend)
- `make` (optional but recommended)

### 1. Start infrastructure

```bash
make dev
```

Or without Make:

```bash
docker compose -f infrastructure/docker/docker-compose.yml up -d
```

Infrastructure starts in dependency order: PostgreSQL instances → Keycloak → RabbitMQ.

### 2. Start backend services

Open four terminals (or run via IDE):

```bash
cd services/account-service      && ./mvnw spring-boot:run
cd services/transaction-service  && ./mvnw spring-boot:run
cd services/notification-service && ./mvnw spring-boot:run
cd services/api-gateway          && ./mvnw spring-boot:run
```

### 3. Start frontend

```bash
cd frontend/web && npm install && npm start
```

Open [http://localhost:4200](http://localhost:4200)

---

## Make Commands

```bash
make dev            # Start infrastructure (Docker)
make stop           # Stop infrastructure
make build          # Build all backend services
make test           # Run tests for all backend services
make dev-frontend   # Start Angular dev server
make build-frontend # Build Angular production bundle
make clean          # Remove build artifacts
make help           # List all available commands
```

---

## Infrastructure Ports

| Component | Port |
|-----------|------|
| Angular SPA | 4200 |
| API Gateway | 8080 |
| account-service | 8081 |
| transaction-service | 8082 |
| notification-service | 8083 |
| Keycloak | 8180 |
| RabbitMQ AMQP | 5672 |
| RabbitMQ Management UI | 15672 |
| PostgreSQL (account) | 5435 |
| PostgreSQL (transaction) | 5436 |
| PostgreSQL (notification) | 5437 |

---

## Default Credentials

| Service | URL | Credentials |
|---------|-----|-------------|
| Keycloak Admin | http://localhost:8180 | `admin` / `admin` |
| App User | http://localhost:4200 | `user@denario.com` / `password` |
| App Admin | http://localhost:4200 | `admin@denario.com` / `admin123` |
| RabbitMQ UI | http://localhost:15672 | `guest` / `guest` |

---

## API Reference (via Gateway — port 8080)

All endpoints require a valid JWT in the `Authorization: Bearer <token>` header unless noted.

### Accounts

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/accounts/me` | List authenticated user's accounts |
| `GET` | `/api/accounts/{id}` | Get account by ID |
| `POST` | `/api/accounts` | Create a new account |
| `GET` | `/api/accounts/balance?iban={iban}` | Check balance by IBAN |

### Transactions

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/transactions/transfer` | Initiate a fund transfer |
| `GET` | `/api/transactions/history` | Paginated transaction history |
| `GET` | `/api/transactions/history/{iban}` | Transactions filtered by IBAN |

### Notifications

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/notifications` | Paginated notification history |
| `PATCH` | `/api/notifications/read-all` | Mark all notifications as read |
| `WS` | `/ws` | WebSocket STOMP endpoint |

Subscribe to `/user/queue/notifications` after connecting for real-time events.

---

## Security Model

Authentication is handled by Keycloak using the **Authorization Code + PKCE** flow.

```
Angular SPA  ──► Keycloak (auth code + PKCE) ──► JWT
Angular SPA  ──► API Gateway (Bearer JWT) ──► services validate via JWKS
```

Roles defined in the `denario` realm:

| Role | Access |
|------|--------|
| `ROLE_USER` | Standard customer — own accounts and transactions |
| `ROLE_ADMIN` | All accounts, admin endpoints |

---

## CI/CD

Two independent pipelines, each triggered only when relevant code changes:

| Pipeline | Trigger | Jobs |
|----------|---------|------|
| `ci-services.yml` | `services/**` | build → test → verify |
| `ci-frontend.yml` | `frontend/**` | lint → test → build |

---

## Why "Denario"?

The *denarius* was the standard silver coin of ancient Rome — the backbone of one of history's most sophisticated economies, enabling trade across a continent for over five centuries. Denario brings that same spirit of reliability and precision to modern distributed systems.
