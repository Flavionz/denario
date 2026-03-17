# ADR 002 — Keycloak for Authentication and Authorization

**Date:** 2024-01
**Status:** Accepted

## Context

Each microservice needs to authenticate requests and authorize users by role (ROLE_USER, ROLE_ADMIN). Options considered:

1. **Custom JWT implementation** — build token issuance and validation from scratch in each service
2. **Keycloak** — dedicated Identity Provider (IdP) with OAuth2/OIDC support
3. **Auth0 / Okta** — managed cloud IdP

## Decision

I use **Keycloak 23** as the central Identity Provider, with each service acting as an OAuth2 resource server validating JWTs issued by Keycloak.

## Rationale

**Against custom JWT:** Implementing token issuance, rotation, refresh, PKCE, and multi-service validation correctly is non-trivial and introduces security risk. Rolling custom auth in a banking system is an antipattern.

**Against managed cloud IdP (Auth0/Okta):** Introduces an external dependency and cost in a fully self-hosted system. For a portfolio and on-premise deployment, self-hosted is preferred.

**For Keycloak:**
- Industry-standard OAuth2/OIDC out of the box (PKCE code flow for SPA)
- Centralized user management, role assignment, and realm configuration
- Each service only needs `spring-boot-starter-oauth2-resource-server` — JWT validation is a one-liner
- Realm configuration is version-controlled as `denario-realm.json` and auto-imported at startup

## Consequences

- **Positive:** Zero custom auth code in business services. Role extraction from `realm_access.roles` is standardized across all services. Keycloak admin UI enables user and role management without code changes.
- **Negative:** Adds Keycloak as an infrastructure dependency (mitigated by Docker Compose health checks and service dependency ordering).
- **Token flow:** Angular SPA uses Authorization Code + PKCE via `angular-oauth2-oidc`. All backend services validate the JWT signature against Keycloak's JWKS endpoint.
