# Denario — Developer convenience commands
# Usage: make <target>

DOCKER_COMPOSE = docker compose -f infrastructure/docker/docker-compose.yml
SERVICES       = account-service transaction-service notification-service api-gateway

.PHONY: help dev stop restart logs build test build-frontend dev-frontend

help: ## Show available commands
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

# ── Infrastructure ────────────────────────────────────────────────────────────

dev: ## Start all infrastructure (postgres, keycloak, rabbitmq)
	$(DOCKER_COMPOSE) up -d
	@echo "Infrastructure running. Services available at:"
	@echo "  Keycloak:        http://localhost:8180  (admin/admin)"
	@echo "  RabbitMQ UI:     http://localhost:15672 (guest/guest)"
	@echo "  Account DB:      localhost:5435"
	@echo "  Transaction DB:  localhost:5436"
	@echo "  Notification DB: localhost:5437"

stop: ## Stop all infrastructure
	$(DOCKER_COMPOSE) down

restart: ## Restart all infrastructure
	$(DOCKER_COMPOSE) down && $(DOCKER_COMPOSE) up -d

logs: ## Tail infrastructure logs
	$(DOCKER_COMPOSE) logs -f

# ── Backend Services ──────────────────────────────────────────────────────────

build: ## Build all backend services (skip tests)
	@for service in $(SERVICES); do \
		echo "Building $$service..."; \
		cd services/$$service && ./mvnw package -DskipTests -q && cd ../..; \
	done
	@echo "All services built."

test: ## Run tests for all backend services
	@for service in $(SERVICES); do \
		echo "Testing $$service..."; \
		cd services/$$service && ./mvnw test -Dspring.profiles.active=test && cd ../..; \
	done

# ── Frontend ──────────────────────────────────────────────────────────────────

dev-frontend: ## Start Angular dev server
	cd frontend/web && npm start

build-frontend: ## Build Angular production bundle
	cd frontend/web && npm run build

test-frontend: ## Run Angular unit tests
	cd frontend/web && npm run test -- --no-watch --browsers=ChromeHeadless

# ── Utilities ─────────────────────────────────────────────────────────────────

clean: ## Remove all build artifacts
	@for service in $(SERVICES); do \
		cd services/$$service && ./mvnw clean -q && cd ../..; \
	done
	@echo "Build artifacts removed."
