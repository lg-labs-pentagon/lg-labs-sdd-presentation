# Tareas — SPEC-ORDER-001

> Descomposición ejecutable. Cada tarea referencia los AC que cubre.

| # | Tarea | AC cubiertos | Estimación |
|---|-------|--------------|------------|
| T1 | Definir entidades de dominio (`Order`, `OrderLine`, `Restaurant`, `MenuItem`, `OperatingHours`) | base | 2h |
| T2 | Definir puertos (`RestaurantRepository`, `MenuItemRepository`, `OrderRepository`, `Clock`) | base | 1h |
| T3 | Implementar `OrderService.createOrder` — happy path | AC-1, AC-7 | 2h |
| T4 | Validación: lista vacía | AC-6 | 30min |
| T5 | Validación: cantidad inválida | AC-5 | 30min |
| T6 | Validación: items inexistentes/inactivos | AC-4 | 1h |
| T7 | Validación: items de distintos restaurantes | AC-2 | 1h |
| T8 | Validación: restaurante cerrado (con clock inyectado) | AC-3 | 1h |
| T9 | Atomicidad de persistencia | AC-8 | 2h |
| T10 | Tests unit completos mapeados 1:1 a AC | todos | 3h |
| T11 | Test de integración happy path + atomicidad | AC-1, AC-8 | 2h |
| T12 | Documentar contrato público (Javadoc / TSDoc) referenciando spec | — | 1h |

**Total estimado**: ~17h por lenguaje.

**Definition of Done**:
- Todos los AC tienen al menos 1 test verde.
- Cobertura de líneas en `OrderService` ≥ 95%.
- PR referencia `SPEC-ORDER-001`.
- Code review aprobada por al menos 1 senior.
