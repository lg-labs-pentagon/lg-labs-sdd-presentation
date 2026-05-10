---
marp: true
theme: default
paginate: true
size: 16:9
header: 'Spec-Driven Development'
footer: '30 min · Senior Devs'
style: |
  section { font-size: 24px; }
  h1 { color: #2563eb; }
  code { background: #f3f4f6; padding: 2px 6px; border-radius: 4px; }
  table { font-size: 0.85em; }
---

<!-- _class: lead -->

# Spec-Driven Development

### Code became cheap. Intent did not.

---

## Objectives (30 min)

By the end of this talk you will:

1. **Distinguish** SDD from TDD/BDD/DDD.
2. **Recognize** a well-written spec vs a poor one.
3. **Apply** the 10 non-negotiable rules.

---

## Base terms — part 1

- **Spec** — *what* the system does and *why*, in verifiable terms.
- **Requirement** — business need.
- **User Story** — narrative format (`As X I want Y so that Z`).
- **Acceptance Criteria (AC)** — measurable conditions.

> User story = what the user wants.
> Spec = what the system must do and how to verify it.

---

## Base terms — part 2

- **Intent vs Implementation**
  - Intent = what problem we're solving
  - Implementation = how we solve it
- **Contract** — interface, schema, OpenAPI, types.
  - First technical artifact derived from a spec.

---

## SDD vs ATDD vs TDD vs BDD vs DDD

| Methodology | Central question | Artifact |
|---|---|---|
| TDD | Does the code work? | Unit test |
| ATDD | Does it meet business AC? | Acceptance test |
| BDD | Is behavior correct? | Gherkin scenario |
| DDD | Does the model fit the domain? | Model |
| **SDD** | **What must it do and why?** | **Spec** |

SDD **orchestrates** the others. Does not replace them.
ATDD ⊂ SDD: acceptance tests are a byproduct of the spec.

---

## What is Spec-Driven Development?

> SDD is the practice of treating the **specification** as the **primary and durable** artifact of the system, from which code, tests, contracts, and documentation are derived.

**Implication**: spec changes → code regenerates.
Code changes without spec → process bug.

---

## Why now?

- AI agents generate code **at scale**.
- The bottleneck is no longer writing code.
- It's **specifying intent correctly**.
- No clear spec → hallucination, misalignment, rework.

**SDD = contract between humans, agents, and systems.**

---

## Vibe-Coding vs SDD

- **Vibe-Coding** — prompt → code → tweak the *vibes*. No contract, no verification.
- **SDD** — intent → spec → code → verification against AC.
- **They coexist**: vibes for throwaway prototypes; SDD for what ships to production.

---

## The SDD cycle

```
   Intent ──► Spec ──► Plan ──► Tasks ──► Code ──► Verify
     ▲                                                │
     └────────────────────────────────────────────────┘
```

The loop is **NOT** an excuse for spec-washing.
If you discover something new → **update spec first**.

---

## The 5 phases + owners

| Phase | Output | Owner |
|---|---|---|
| Specify | `spec.md` | Tech Lead + PM |
| Plan | `plan.md` | Tech Lead |
| Tasks | `tasks.md` | Tech Lead + Devs |
| Implement | code + tests | Devs (+ AI) |
| Verify | AC report | QA + Devs |

---

## Tasks Layer — the secret sauce

- Vibe-coding fails because it jumps from **Feature → Code**.
- SDD requires **decomposition**: Spec → Plan → **Tasks** → Code.
- Every task has its own input, output, and AC.

> **The Golden Rule**: every task must fit in a diff a human can review in **< 5 min**.

---

## Example case

**Domain**: food ordering system.

**Entities**: Customer, Restaurant, MenuItem, Order, OrderLine, Payment, OperatingHours.

**Feature**:
> Create an order validating that all items belong to the **same restaurant** and that the restaurant is **open**.

---

## Anti-example

```
Ticket FOOD-1234
Title: Create order
Description: User must be able to create an order
with multiple items. Validate that everything is OK.
```

❌ No context · ❌ "Everything OK" not verifiable
❌ No edge cases · ❌ No glossary

---

## Well-written SDD spec — structure

1. **Context** — why it exists
2. **Behavior** — clear rules
3. **Acceptance Criteria** — verifiable, numbered
4. **Out of Scope** — what it does NOT do
5. **Glossary** — domain terms

See `examples/01-spec-create-order.md`

---

## Derived contract — Java

```java
public interface OrderService {
  Order createOrder(
    CustomerId customerId,
    List<OrderLineRequest> lines
  ) throws RestaurantClosedException,
           MixedRestaurantsException,
           ItemNotFoundException;
}
```

Typed exceptions = part of the contract.

---

## Derived contract — TypeScript

```ts
export type OrderError =
  | { kind: 'RestaurantClosed' }
  | { kind: 'MixedRestaurants' }
  | { kind: 'ItemNotFound'; itemId: string };

export interface OrderService {
  createOrder(
    customerId: CustomerId,
    lines: OrderLineRequest[]
  ): Promise<Result<Order, OrderError>>;
}
```

---

## Verification: AC ↔ Tests

| AC | Test |
|---|---|
| AC-1 valid order | `createOrder_withValidItems_returnsOrder` |
| AC-2 mixed | `..._withMixedRestaurants_throws...` |
| AC-3 closed | `..._whenRestaurantClosed_throws...` |
| AC-4 unknown item | `..._withUnknownItem_throws...` |

> If an AC has no test → the feature is **not done**.

---

## Mandatory rules (1-5)

1. **No code without an approved spec** (includes AI prompts).
2. Spec describes **what/why**, never **how**.
3. AC must be **verifiable and measurable**.
4. Specs **versioned in the repo**.
5. Behavior change → **Spec-first PR**.

---

## Mandatory rules (6-10)

6. Each PR **references its spec**.
7. Ambiguous specs are **rejected in review**.
8. Tests **derive from the spec**, not the implementation.
9. One spec = **bounded scope**.
10. Shared **domain glossary**.

---

## Anti-patterns

- **Spec-as-design** — describes classes/methods. ❌
- **Spec-washing** — written post-hoc. ❌
- **UI-spec** — describes screens. ❌
- **Spec without metrics** — "must be fast". ❌

> Instead of "fast": **p95 < 200ms at 100 RPS**.

---

## Domain glossary

| Term | Definition |
|---|---|
| Customer | Registered user who can order |
| Restaurant | Establishment with menu and hours |
| MenuItem | Sellable product of a Restaurant |
| Order | Purchase request with N OrderLines |
| OrderLine | Quantity + MenuItem + frozen price |
| Payment | Transaction associated with Order |
| OperatingHours | Windows when Restaurant accepts Orders |

---

## 4-week adoption

- **W1**: pilot `spec-template.md` on 1 feature.
- **W2**: Spec-first PR in 1 squad.
- **W3**: checklist in global PR template.
- **W4**: retro + metrics + rollout.

**Metrics**: % PRs with spec · ambiguity bugs · onboarding time.

---

<!-- _class: lead -->

# Questions?

**One feature. One spec. Let's see what happens.**
No ceremony, no committee. Just try it.
