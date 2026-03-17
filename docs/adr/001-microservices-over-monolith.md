# ADR 001 — Microservices over Monolith

**Date:** 2024-01
**Status:** Accepted

## Context

Denario is a digital banking platform requiring independent scalability across different domains: account management, financial transactions, and real-time notifications. The question was whether to build a single monolithic application or decompose into separate services.

## Decision

I chose a **microservices architecture** with four independent services: `api-gateway`, `account-service`, `transaction-service`, and `notification-service`.

## Rationale

| Concern | Monolith | Microservices (chosen) |
|---------|----------|----------------------|
| Independent scaling | No — scale everything | Yes — scale only transaction-service under load |
| Fault isolation | No — one crash takes everything down | Yes — circuit breakers contain failures |
| Deployment independence | No — redeploy the whole app | Yes — deploy only the changed service |
| Team autonomy | Low — coordination required for every change | High — each service has a clear owner boundary |
| Technology flexibility | Low — single stack | High — each service can evolve independently |

Banking domains are naturally bounded: account lifecycle, payment processing, and notifications have distinct responsibilities, failure modes, and scaling profiles. Transactions, for example, can experience high burst load during business hours without affecting notification delivery.

## Consequences

- **Positive:** Independent deployability, resilience via circuit breakers, clear domain boundaries.
- **Negative:** Increased operational complexity (multiple processes, databases, message queues). Accepted because Docker Compose handles local orchestration and the infrastructure complexity is appropriate for a production banking system.
- **Trade-off:** Distributed transactions across services are handled via eventual consistency (RabbitMQ events) rather than ACID guarantees, which is the standard approach at this scale.
