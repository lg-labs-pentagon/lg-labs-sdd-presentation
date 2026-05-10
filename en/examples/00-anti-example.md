# Anti-example: how NOT to write a spec

> Typical vague-ticket format that does **not** qualify as an SDD spec.

---

## Ticket FOOD-1234

**Title**: Create order

**Description**:
The user must be able to create an order with multiple items. Validate that everything is OK and that the restaurant exists. Show error message if there's a problem. Also consider that items that don't exist can't be ordered.

**Assigned to**: backend team
**Priority**: high

---

## Why this fails as a spec

| Problem | Detail |
|---|---|
| No context | Doesn't explain the "why". Is this a new business line? Replacing something? |
| "Everything OK" | Not verifiable. What gets validated exactly? |
| No edge cases | What if the restaurant is closed? Items from different restaurants? Stock? |
| No glossary | Does "order" and "item" mean the same to PM, backend, and frontend? |
| No acceptance criteria | No way to know when it's done. |
| No out-of-scope | Does it include payment? Notifications? Tracking? |
| "Show message" | Mixes intent with UI. A spec must not describe UI. |

---

## Predictable consequences

1. **Divergent implementation**: 3 devs would build 3 different things.
2. **Weak tests**: only happy-path covered.
3. **Production bugs** from un-considered cases.
4. **Rework**: 2-3 follow-up PRs to "complete" the feature.
5. **Code review debate** about behavior that should have been defined upfront.

---

> Compare with `01-spec-create-order.md` for the SDD version.
