# Diagramas reutilizables (Mermaid)

> Embebibles en Marp si tu render lo soporta, o copiar/exportar manualmente.

---

## SDD Cycle

```mermaid
flowchart LR
    Intent --> Spec
    Spec --> Plan
    Plan --> Tasks
    Tasks --> Code
    Code --> Verify
    Verify -->|new findings| Spec
```

---

## SDD vs other methodologies

```mermaid
flowchart TD
    SDD[Spec-Driven Development<br/>WHAT & WHY]
    SDD --> ATDD[ATDD: meets business AC?]
    SDD --> TDD[TDD: code works?]
    SDD --> BDD[BDD: behavior correct?]
    SDD --> DDD[DDD: model fits domain?]
```

---

## Order Domain

```mermaid
classDiagram
    class Customer {
        id
    }
    class Restaurant {
        id
        timezone
        operatingHours
        isOpenAt(instant)
    }
    class MenuItem {
        id
        restaurantId
        price
        active
    }
    class Order {
        id
        customerId
        restaurantId
        status
        total()
    }
    class OrderLine {
        menuItemId
        quantity
        frozenUnitPrice
        subtotal()
    }
    Customer "1" -- "*" Order
    Restaurant "1" -- "*" MenuItem
    Restaurant "1" -- "*" Order
    Order "1" *-- "1..*" OrderLine
    OrderLine --> MenuItem
```

---

## Spec → Code → Verify (with AC mapping)

```mermaid
flowchart LR
    SPEC[spec.md<br/>AC-1..AC-8] --> CONTRACT[OrderService<br/>interface]
    CONTRACT --> IMPL[OrderService<br/>implementation]
    SPEC --> TESTS[Tests<br/>1:1 with AC]
    IMPL --> TESTS
    TESTS --> VERIFY{All AC<br/>green?}
    VERIFY -->|yes| DONE[Feature done]
    VERIFY -->|no| IMPL
```

---

## Cómo renderizar

- **VS Code**: extensión "Markdown Preview Mermaid Support".
- **Marp**: requiere plugin `marp-plugin-mermaid` o exportar a PNG.
- **CLI**: `npx -y @mermaid-js/mermaid-cli -i diagrams.md -o out.svg`.
