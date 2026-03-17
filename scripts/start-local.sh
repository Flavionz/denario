#!/usr/bin/env bash
# start-local.sh — Start the full Denario stack for local development
# Usage: ./scripts/start-local.sh

set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "Starting Denario infrastructure..."
docker compose -f "$ROOT_DIR/infrastructure/docker/docker-compose.yml" up -d

echo "Waiting for services to be healthy..."
sleep 5

echo ""
echo "Infrastructure ready:"
echo "  Keycloak:        http://localhost:8180  (admin / admin)"
echo "  RabbitMQ UI:     http://localhost:15672 (guest / guest)"
echo "  Account DB:      localhost:5435"
echo "  Transaction DB:  localhost:5436"
echo "  Notification DB: localhost:5437"
echo ""
echo "To start backend services, run in separate terminals:"
echo "  cd services/account-service     && ./mvnw spring-boot:run"
echo "  cd services/transaction-service && ./mvnw spring-boot:run"
echo "  cd services/notification-service && ./mvnw spring-boot:run"
echo "  cd services/api-gateway         && ./mvnw spring-boot:run"
echo ""
echo "To start the frontend:"
echo "  cd frontend/web && npm start"
