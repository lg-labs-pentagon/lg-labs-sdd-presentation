# Tasks — SPEC-ORDER-001

> Executable breakdown. Each task references the ACs it covers.

| # | Task | ACs covered | Estimate |
|---|------|-------------|----------|
| T1 | Define domain entities (`Order`, `OrderLine`, `Restaurant`, `MenuItem`, `OperatingHours`) | base | 2h |
| T2 | Define ports (`RestaurantRepository`, `MenuItemRepository`, `OrderRepository`, `Clock`) | base | 1h |
| T3 | Implement `OrderService.createOrder` — happy path | AC-1, AC-7 | 2h |
| T4 | Validation: empty list | AC-6 | 30min |
| T5 | Validation: invalid quantity | AC-5 | 30min |
| T6 | Validation: non-existent/inactive items | AC-4 | 1h |
| T7 | Validation: items from different restaurants | AC-2 | 1h |
| T8 | Validation: closed restaurant (with injected clock) | AC-3 | 1h |
| T9 | Atomic persistence | AC-8 | 2h |
| T10 | Full unit tests mapped 1:1 to AC | all | 3h |
| T11 | Integration test: happy path + atomicity | AC-1, AC-8 | 2h |
| T12 | Document public contract (Javadoc / TSDoc) referencing spec | — | 1h |

**Total estimate**: ~17h per language.

**Definition of Done**:
- Every AC has at least 1 green test.
- Line coverage in `OrderService` ≥ 95%.
- PR references `SPEC-ORDER-001`.
- Code review approved by at least 1 senior.
