# Spec: Create Order

**ID**: SPEC-ORDER-001
**Version**: 1.0.0
**Status**: Approved
**Date**: 2026-05-07
**Owners**: Tech Lead Orders · PM Marketplace

---

## 1. Context

Customers on the platform need to create food orders from a Restaurant's catalog. This is the first feature of the purchase flow; it blocks integration with the Payment module and delivery.

**Why it matters**: without this capability, the rest of the marketplace cannot operate. It's the transactional core of the business.

**Key decision**: an order belongs to **a single Restaurant**. Multi-restaurant orders are not supported in this version (see Out of Scope).

---

## 2. Behavior

The system allows an authenticated **Customer** to create an **Order** containing one or more **OrderLines**. An OrderLine references a **MenuItem** and a quantity.

**Business rules**:

- **R1**: All MenuItems in an Order must belong to the **same Restaurant**.
- **R2**: The Order can only be created if the Restaurant is **open** per its `OperatingHours` at creation time (Restaurant's timezone).
- **R3**: Each referenced MenuItem must **exist** and be **active** in the Restaurant's catalog.
- **R4**: Quantity per OrderLine must be an integer **≥ 1**.
- **R5**: The Order must contain **at least 1 OrderLine**.
- **R6**: Each OrderLine's price is **frozen** to the MenuItem's current price at creation.
- **R7**: The Order is created in `PENDING_PAYMENT` status.

---

## 3. Acceptance Criteria

| ID | Criterion | Verifiable via |
|----|-----------|----------------|
| **AC-1** | Given a Customer, an open Restaurant, and N valid items from the same Restaurant, when the order is created, then a `PENDING_PAYMENT` Order is returned with N OrderLines and frozen prices. | Integration + unit test |
| **AC-2** | Given a Customer and a list of items belonging to **different** Restaurants, when creation is attempted, then it fails with `MixedRestaurants` error and nothing is persisted. | Unit test |
| **AC-3** | Given a **closed** Restaurant (outside `OperatingHours`), when creation is attempted, then it fails with `RestaurantClosed` error and nothing is persisted. | Unit test with injected clock |
| **AC-4** | Given at least one **non-existent** or **inactive** MenuItem in the list, when creation is attempted, then it fails with `ItemNotFound(itemId)` for the first problematic item and nothing is persisted. | Unit test |
| **AC-5** | Given an OrderLine with quantity ≤ 0, when creation is attempted, then it fails with `InvalidQuantity` error and nothing is persisted. | Unit test |
| **AC-6** | Given an empty OrderLines list, when creation is attempted, then it fails with `EmptyOrder` error and nothing is persisted. | Unit test |
| **AC-7** | Order total = sum of `(quantity × frozenUnitPrice)` per OrderLine. | Unit test |
| **AC-8** | Creation is **atomic**: either Order + all OrderLines persist, or nothing does. | Integration test with simulated failure |

---

## 4. Out of Scope

- Coupon/discount application (separate spec).
- Stock/inventory management.
- Multi-restaurant orders.
- Restaurant notifications.
- Delivery fee calculation.
- Payment processing (Order is left in `PENDING_PAYMENT`).
- UI / HTTP endpoints (this spec is for the domain; HTTP contract has its own spec).

---

## 5. Glossary

| Term | Definition |
|---|---|
| **Customer** | Authenticated registered user who can create Orders. |
| **Restaurant** | Establishment with menu, location, and `OperatingHours`. |
| **MenuItem** | Sellable product belonging to a single Restaurant. Has price and `active` flag. |
| **Order** | Purchase request created by a Customer; contains 1..N OrderLines and belongs to a single Restaurant. |
| **OrderLine** | `(menuItemId, quantity, frozenUnitPrice)`. |
| **OperatingHours** | Set of weekly windows (e.g., Mon 11:00-23:00) in the Restaurant's timezone. |
| **PENDING_PAYMENT** | Initial Order status after successful creation. |

---

## 6. Open Questions (resolved before Implement)

- [x] What if price changed while customer was building the cart? → **Frozen at Order creation**. If it differs from what the customer saw, the front must re-validate before calling.
- [x] Do we support concurrent Orders from the same Customer? → **Yes**, no limit in this version.

---

## 7. Change Log

| Version | Date | Change |
|---|---|---|
| 1.0.0 | 2026-05-07 | Initial approved version. |
