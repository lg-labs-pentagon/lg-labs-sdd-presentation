# Spec Template

**ID**: SPEC-XXX-NNN
**Versión**: 0.1.0
**Estado**: Draft | In Review | Approved | Deprecated
**Fecha**: YYYY-MM-DD
**Owners**: <Tech Lead> · <PM>

---

## 1. Context

> Por qué existe esta feature. Qué problema de negocio resuelve. A qué se conecta. Decisiones de alto nivel ya tomadas.

---

## 2. Behavior

> Qué hace el sistema, descrito como **reglas de negocio** numeradas.
> NO describir clases, métodos ni endpoints. Solo comportamiento observable.

- **R1**: ...
- **R2**: ...

---

## 3. Acceptance Criteria

> Cada AC debe ser **verificable** y mapeable a uno o más tests.

| ID | Criterio | Verificable mediante |
|----|----------|----------------------|
| **AC-1** | Dado ... cuando ... entonces ... | Test unit / integración |
| **AC-2** | ... | ... |

---

## 4. Out of Scope

> Qué NO hace este spec. Importante para evitar scope-creep.

- ...
- ...

---

## 5. Glossary

| Término | Definición |
|---------|------------|
| ... | ... |

---

## 6. Open Questions

> Marca como `[x]` cuando se resuelvan. Una spec **no se aprueba** con preguntas abiertas.

- [ ] ...

---

## 7. Change Log

| Versión | Fecha | Cambio |
|---------|-------|--------|
| 0.1.0 | YYYY-MM-DD | Versión inicial. |

---

## Checklist antes de aprobar

- [ ] Cada AC es verificable (no incluye palabras como "fácil", "rápido", "intuitivo" sin métrica).
- [ ] Glosario incluye todos los términos del dominio mencionados.
- [ ] Out of Scope explícito.
- [ ] Open Questions resueltas.
- [ ] No describe implementación (clases, métodos, endpoints, UI).
- [ ] Tamaño: cabe leerlo en menos de 5 minutos.
