# ADR 003 — RabbitMQ for Asynchronous Inter-Service Events

**Date:** 2024-01
**Status:** Accepted

## Context

When a transaction completes or an account is created, the `notification-service` must be informed to deliver real-time alerts. Options for inter-service communication:

1. **Synchronous HTTP** — `transaction-service` calls `notification-service` directly via REST
2. **RabbitMQ** — message broker with topic exchanges and durable queues
3. **Apache Kafka** — distributed event streaming platform

## Decision

I use **RabbitMQ 3.12** with topic exchanges for all event-driven communication between services.

## Rationale

**Against synchronous HTTP for notifications:**
Availability coupling — if `notification-service` is down, the transaction fails. Notifications are not in the critical path of a payment; a transaction must succeed regardless of whether the user receives an alert.

**Against Kafka:**
Kafka is the right choice for high-throughput event streaming (millions of events/day, audit logs, event sourcing). For Denario's use case (user-facing notifications at banking transaction volumes), Kafka adds operational overhead (ZooKeeper/KRaft, partition management, consumer groups) without meaningful benefit.

**For RabbitMQ:**
- Lightweight and operationally simple — single Docker container, management UI included
- Topic exchanges provide flexible routing (`account.created`, `transaction.created`) without consumer knowledge of producers
- Spring AMQP integration is first-class: `@RabbitListener` + `Jackson2JsonMessageConverter` requires minimal boilerplate
- Message durability and acknowledgment ensure no notification is lost even if the service restarts

## Event topology

```
account-service  ──► account.exchange  ──► account.created.queue   ──► notification-service
transaction-service ► transaction.exchange ► transaction.created.queue ► notification-service
```

## Consequences

- **Positive:** Temporal decoupling — `notification-service` processes events at its own pace. `transaction-service` is not blocked by notification delivery. Adding a new consumer (e.g., an audit service) requires only a new binding, zero changes to producers.
- **Negative:** Eventual consistency — the user may see the notification a few milliseconds after the transaction completes. Acceptable for a notification system; not acceptable for balance updates (which use synchronous HTTP for that reason).
- **Design rule:** Synchronous HTTP between services is reserved for operations in the critical path (e.g., balance check before transfer). Everything else uses RabbitMQ.
