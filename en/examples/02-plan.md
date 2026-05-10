# Implementation Plan — SPEC-ORDER-001

> Derives from `01-spec-create-order.md`. Defines **how** to build it without changing **what**.

---

## Architecture

- **Domain layer**: pure entities (`Order`, `OrderLine`, `Restaurant`, `MenuItem`) with no external deps.
- **Application service**: `OrderService` orchestrates validation and persistence.
- **Ports**:
  - `RestaurantRepository.findById(id) → Optional<Restaurant>`
  - `MenuItemRepository.findByIds(ids) → List<MenuItem>`
  - `OrderRepository.save(order) → Order`
  - `Clock.now() → Instant`
- **Adapters**: concrete impls (DB, in-memory for tests).

## Stack & constraints

- **Java**: 21, JUnit 5, Mockito.
- **TypeScript**: Node 20, Vitest, no heavy frameworks.
- No network access in domain.
- Injected clock (no direct `Instant.now()` in domain code).
- Atomic operation (handled in adapter; contract exposes via `OrderRepository.save`).

## Technical decisions

| Decision | Justification |
|----------|---------------|
| Typed exceptions (Java) / `Result<T,E>` (TS) | Errors are part of the contract, not side effects. |
| Frozen price on `OrderLine` | Satisfies R6, avoids future inconsistencies. |
| Validate items exist before restaurant open | More useful error message to user. |
| Stop at first invalid item | Simplicity; future iterations can return list. |

## Risks

- **Stock concurrency**: out of scope, but a future spec must consider locks/versioning.
- **Timezones**: `Restaurant` must store IANA tz; hour comparison uses that tz, not UTC.
