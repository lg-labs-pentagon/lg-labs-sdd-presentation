# Presentation Guide — Spec-Driven Development

> 30-minute talk for senior developers. Speaker notes per slide, additional context, and prepared Q&A.

---

## Slide 1 — Title + Hook

**Hook (say verbatim)**:
> "In the era of AI agents, code became cheap. Clear intent did not."

**Notes**: pause 2 seconds after the hook. Clarify this isn't about "more documentation" — it's about changing **where** the truth of the system lives.

---

## Slide 2 — Learning objectives

By the end of 30 min, the team should be able to:

1. Distinguish SDD from TDD/BDD/DDD and when to apply each.
2. Recognize a well-written spec vs a poorly-written one.
3. Know the 10 non-negotiable rules to adopt SDD.

**Notes**: ask by show of hands who already practices TDD/BDD to calibrate pace.

---

## Slide 3 — Base terms (part 1)

**Spec**: description of **what** the system does and **why**, in verifiable terms. Does not describe the how.

**Requirement**: business need. A spec formalizes it.

**User Story**: narrative format (`As X I want Y so that Z`). Useful to discover requirements, **insufficient** as a spec.

**Acceptance Criteria (AC)**: measurable conditions that determine if the spec is met.

**Notes**: key takeaway — *user story answers "what does the user want"; spec answers "what must the system do and how do we verify it"*.

---

## Slide 4 — Base terms (part 2)

**Intent vs Implementation**:
- Intent = what problem we're solving
- Implementation = how we solve it in code

**Contract**: formal agreement between components (interface, schema, OpenAPI, types). Bridge between spec and code.

**Notes**: a contract is the first technical artifact derived from a spec. If your spec doesn't produce a clear contract, it's poorly written.

---

## Slide 5 — SDD vs ATDD vs TDD vs BDD vs DDD

| Methodology | Central question | Main artifact |
|---|---|---|
| **TDD** | Does the code work? | Unit test |
| **ATDD** | Does it meet business acceptance criteria? | Acceptance test |
| **BDD** | Is behavior correct? | Gherkin scenario |
| **DDD** | Does the model reflect the domain? | Domain model |
| **SDD** | What must the system do and why? | **Executable spec** |

**Notes**:
- SDD **does not replace** the others. It **orchestrates** them. Spec is the source; tests, scenarios, and models derive from it.
- **ATDD vs SDD**: ATDD centers on acceptance tests as the development driver (business + QA + dev agree on tests before coding). SDD is broader: the spec contains the AC, but also context, behavior rules, glossary, and out-of-scope. ATDD's acceptance tests can be seen as a **byproduct** of the SDD spec.
- **ATDD vs BDD**: BDD uses structured natural language (Given/When/Then in Gherkin); ATDD does not require a specific format. BDD is often considered a practical "implementation" of ATDD.

---

## Slide 6 — What is SDD

**Operational definition**:
> Spec-Driven Development is the practice of treating the **specification** as the primary and durable artifact of the system, from which code, tests, contracts, and documentation are derived.

**Key implication**: if the spec changes, code is regenerated/refactored. If code changes without spec, that's a process bug.

**Notes**: emphasize "durable". Code gets rewritten (frameworks, languages, AI agents). The spec survives.

---

## Slide 7 — Why now

- AI agents generate code at massive speed.
- The bottleneck is no longer writing code, it's **specifying intent correctly**.
- Without clear spec: agent hallucinates, team misaligns, rework explodes.
- SDD is the contract between humans, agents, and systems.

**Notes**: optional citation — Sean Grove ("The New Code") argues specs will be the main artifact developers write.

---

## Slide 7b — Vibe-Coding vs SDD

- **Vibe-Coding** — prompt → code → tweak the *vibes*. No contract, no verification.
- **SDD** — intent → spec → code → verification against AC.
- **They coexist**: vibes for throwaway prototypes; SDD for what ships to production.

**Notes**: the term *vibe-coding* was popularized by Andrej Karpathy (2025), describing the flow of "accepting whatever the LLM suggests and just vibing along." Great for weekend hacks and throwaway code, dangerous in production: no spec means no way to verify, no contract means no way to evolve. SDD doesn't reject AI — it channels it through a durable artifact.

---

## Slide 8 — The SDD cycle

```
Intent → Spec → Plan → Tasks → Code → Verify
   ↑                                      |
   └──────────────────────────────────────┘
```

It's a loop. Verification feeds back into the spec when we discover missing cases.

**Notes**: stress that the loop is **not** for spec-washing. If something is discovered, update the spec **before** touching new code.

---

## Slide 9 — The 5 phases + responsibilities

| Phase | Output | Primary owner |
|---|---|---|
| Specify | spec.md | Tech Lead + PM |
| Plan | plan.md (architecture, stack, constraints) | Tech Lead |
| Tasks | tasks.md (executable breakdown) | Tech Lead + Devs |
| Implement | code + tests | Devs (+ AI agents) |
| Verify | AC coverage report | QA + Devs |

**Notes**: in small teams one person covers multiple roles, but **artifacts remain distinct**.

---

## Slide 9b — Tasks Layer: the secret sauce

- Vibe-coding fails because it jumps from **Feature → Code** with no verifiable intermediate steps.
- SDD requires **decomposition**: Spec → Plan → **Tasks** → Code.
- Every task has its own input, output, and acceptance criteria.

> **The Golden Rule**: every task must fit in a diff a human can review in under 5 minutes.

**Notes**: this is the slide where you win or lose the technical audience. The Tasks layer is the real difference between "I asked the AI to do it" and "I worked with the AI." Small tasks → cheap review → compounding trust. Large tasks → impossible review → you slide back into vibe-coding without realizing. If a task doesn't fit in 5 min of review, it's not a task — it's a mini-project, split it.

---

## Slide 10 — Example case

**Domain**: food ordering system.

**Entities**: Customer, Restaurant, MenuItem, Order, OrderLine, Payment, OperatingHours.

**Feature to specify**:
> "Create an order validating that all items belong to the same restaurant and that the restaurant is open."

**Notes**: chosen because it has clear business rules, edge cases, and maps well to contracts.

---

## Slide 11 — Anti-example (how NOT to write a spec)

```
Ticket FOOD-1234
Title: Create order
Description: User must be able to create an order
with multiple items. Validate that everything is OK.
```

**Problems**:
- No context for the why.
- "Everything is OK" is not verifiable.
- No edge cases defined.
- No glossary.

**Notes**: rhetorical question — *how many PRs have you reviewed where the ticket looked like this?*

---

## Slide 12 — Well-written SDD spec

Minimum structure:

1. **Context** — why it exists
2. **Behavior** — what it does, as rules
3. **Acceptance Criteria** — verifiable, numbered
4. **Out of Scope** — what it does NOT do
5. **Glossary** — domain terms

See `examples/01-spec-create-order.md` for the full version.

**Notes**: share the file on screen for 60 seconds. Let them read in silence.

---

## Slide 13 — Derived contract in Java

```java
public interface OrderService {
  Order createOrder(CustomerId customerId, List<OrderLineRequest> lines);
}
```

Plus DTOs, typed exceptions (`RestaurantClosedException`, `MixedRestaurantsException`) and JUnit tests that **map 1:1** to ACs.

See `examples/java/`.

**Notes**: typed exceptions are part of the contract — not implementation details.

---

## Slide 14 — Derived contract in TypeScript

```ts
export interface OrderService {
  createOrder(customerId: CustomerId, lines: OrderLineRequest[]): Promise<Order>;
}
```

Discriminated unions for errors (`Result<Order, OrderError>`), Vitest tests with the same AC mapping.

See `examples/typescript/`.

**Notes**: contrast idiomatic error handling per language. Same spec; slightly different contract.

---

## Slide 15 — Verification: AC ↔ Tests

Mapping table:

| AC | Test |
|---|---|
| AC-1: valid order created | `createOrder_withValidItems_returnsOrder` |
| AC-2: rejects items from different restaurants | `createOrder_withMixedRestaurants_throwsMixedRestaurantsException` |
| AC-3: rejects if restaurant closed | `createOrder_whenRestaurantClosed_throwsRestaurantClosedException` |
| AC-4: rejects unknown items | `createOrder_withUnknownItem_throwsItemNotFoundException` |

**Rule**: if an AC has no test, the feature is not done.

---

## Slide 16 — Mandatory rules (1-5)

1. **No code without an approved spec** (includes AI prompts).
2. Spec describes **what/why**, never **how**.
3. AC must be **verifiable and measurable**.
4. Specs **versioned in the repo** alongside code.
5. Behavior changes → **Spec-first PR** before the code-PR.

**Notes**: rule 5 generates the most resistance. Anticipate: "adds an extra PR"; counter: "avoids 3 rework PRs".

---

## Slide 17 — Mandatory rules (6-10)

6. Each PR **references its spec** (explicit link).
7. Ambiguous specs are **rejected in review** like bad code.
8. Tests **derive from the spec**, not the implementation.
9. One spec = **bounded scope** (size rule: fits one screen).
10. Shared, maintained **domain glossary**.

**Notes**: rule 8 is subtle but critical. Writing the test after the code validates what you did, not what you should have done.

---

## Slide 18 — Anti-patterns

- **Spec-as-design**: the spec describes implementation (classes, methods). Bad.
- **Spec-washing**: writing it post-hoc to comply with process. Useless.
- **UI-spec**: describes screens instead of behavior. Fragile.
- **Spec without metrics**: "must be fast" instead of "p95 < 200ms".

**Notes**: ask the team for examples out loud. Guaranteed all 4 appear.

---

## Slide 19 — Domain glossary

| Term | Definition |
|---|---|
| Customer | Registered user who can order |
| Restaurant | Establishment with menu and hours |
| MenuItem | Sellable product belonging to a Restaurant |
| Order | Purchase request from a Customer with N OrderLines |
| OrderLine | Quantity + MenuItem + price at order time |
| Payment | Transaction associated with an Order |
| OperatingHours | Time windows when a Restaurant accepts Orders |

**Notes**: glossary is part of the spec. Without it, two devs understand "order" differently.

---

## Slide 20 — 4-week adoption + Q&A

**Week 1**: adopt `spec-template.md` for 1 pilot feature.
**Week 2**: require Spec-first PR in 1 squad.
**Week 3**: add checklist to global PR template.
**Week 4**: retrospective, metrics (rework, clarity), expansion.

**Success metrics**:
- % PRs with referenced spec
- Bugs from requirement ambiguity (should drop)
- Time to onboard new features

**Closing**: "One feature. One spec. Let's see what happens. No ceremony, no committee. Just try it."

---

## Prepared Q&A

**Q: Isn't this waterfall in disguise?**
A: No. The spec is alive, versioned, refactorable. What we DON'T do is write code first and document after.

**Q: Doesn't it slow down development?**
A: Short-term, yes (10-20%). Mid-term, reduces rework 30-50% per typical internal studies. Measure in your team.

**Q: Who writes the spec?**
A: Tech lead + PM together. A senior dev can write it alone if the domain is clear.

**Q: What about legacy code without specs?**
A: Don't rewrite. When you touch an area, write the spec of the **current behavior** first (retroactive spec), then change.

**Q: How do AI agents fit in?**
A: The spec is the durable prompt. Agents generate code from it. Without spec, agents hallucinate.
