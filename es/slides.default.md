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

### El código se volvió barato. La intención, no.

---

## Objetivos (30 min)

Al final de esta charla podrás:

1. **Distinguir** SDD de TDD/BDD/DDD.
2. **Reconocer** una spec bien escrita vs una mal escrita.
3. **Aplicar** las 10 reglas no negociables.

---

## Términos base — parte 1

- **Spec** — *qué* hace el sistema y *por qué*, en términos verificables.
- **Requerimiento** — necesidad del negocio.
- **User Story** — formato narrativo (`Como X quiero Y para Z`).
- **Acceptance Criteria (AC)** — condiciones medibles.

> User story = qué quiere el usuario.
> Spec = qué debe hacer el sistema y cómo se verifica.

---

## Términos base — parte 2

- **Intención vs Implementación**
  - Intención = qué problema resolvemos
  - Implementación = cómo lo resolvemos
- **Contrato** — interface, schema, OpenAPI, tipos.
  - El primer artefacto técnico que deriva de una spec.

---

## SDD vs ATDD vs TDD vs BDD vs DDD

| Metodología | Pregunta central | Artefacto |
|---|---|---|
| TDD | ¿El código funciona? | Test unitario |
| ATDD | ¿Cumple los AC del negocio? | Acceptance test |
| BDD | ¿El comportamiento es correcto? | Escenario Gherkin |
| DDD | ¿El modelo refleja el dominio? | Modelo |
| **SDD** | **¿Qué debe hacer y por qué?** | **Spec** |

SDD **orquesta** a las demás. No las reemplaza.
ATDD ⊂ SDD: los acceptance tests son un subproducto de la spec.

---

## ¿Qué es Spec-Driven Development?

> SDD es la práctica de tratar la **especificación** como el artefacto **primario y durable** del sistema, del cual derivan código, tests, contratos y documentación.

**Implicación**: si la spec cambia → el código se regenera.
Si el código cambia sin spec → bug en el proceso.

---

## ¿Por qué ahora?

- Los agentes de IA generan código **masivamente**.
- El cuello de botella ya no es escribir código.
- Es **especificar correctamente la intención**.
- Sin spec clara → alucinaciones, desalineación, rework.

**SDD = contrato entre humanos, agentes y sistemas.**

---

## Vibe-Coding vs SDD

- **Vibe-Coding** — prompt → código → ajusto las *vibes*. Sin contrato, sin verificación.
- **SDD** — intención → spec → código → verificación contra AC.
- **Conviven**: vibes para prototipos desechables; SDD para lo que llega a producción.

---

## El ciclo SDD

```
   Intent ──► Spec ──► Plan ──► Tasks ──► Code ──► Verify
     ▲                                                │
     └────────────────────────────────────────────────┘
```

El loop **NO** es excusa para spec-washing.
Si descubres algo nuevo → **actualiza la spec primero**.

---

## Las 5 fases + responsables

| Fase | Output | Responsable |
|---|---|---|
| Specify | `spec.md` | Tech Lead + PM |
| Plan | `plan.md` | Tech Lead |
| Tasks | `tasks.md` | Tech Lead + Devs |
| Implement | código + tests | Devs (+ IA) |
| Verify | reporte AC | QA + Devs |

---

## Tasks Layer — la salsa secreta

- Vibe-coding falla porque salta de **Feature → Código**.
- SDD exige **descomposición**: Spec → Plan → **Tasks** → Code.
- Cada task tiene su input, output y AC propios.

> **Regla de oro**: cada task debe caber en un diff que un humano revise en **< 5 min**.

---

## Caso de ejemplo

**Dominio**: sistema de pedidos de comida.

**Entidades**: Customer, Restaurant, MenuItem, Order, OrderLine, Payment, OperatingHours.

**Feature**:
> Crear un pedido validando que todos los ítems pertenezcan al **mismo restaurante** y que el restaurante esté **abierto**.

---

## Anti-ejemplo

```
Ticket FOOD-1234
Título: Crear pedido
Descripción: El usuario debe poder crear un pedido
con varios items. Validar que todo esté bien.
```

❌ Sin contexto · ❌ "Todo esté bien" no es verificable
❌ Sin casos borde · ❌ Sin glosario

---

## Spec SDD bien escrita — estructura

1. **Context** — por qué existe
2. **Behavior** — reglas claras
3. **Acceptance Criteria** — verificables, numerados
4. **Out of Scope** — qué NO hace
5. **Glossary** — términos del dominio

Ver `ejemplos/01-spec-crear-pedido.md`

---

## Contrato derivado — Java

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

Excepciones tipadas = parte del contrato.

---

## Contrato derivado — TypeScript

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

## Verificación: AC ↔ Tests

| AC | Test |
|---|---|
| AC-1 pedido válido | `createOrder_withValidItems_returnsOrder` |
| AC-2 mixto | `..._withMixedRestaurants_throws...` |
| AC-3 cerrado | `..._whenRestaurantClosed_throws...` |
| AC-4 item inexistente | `..._withUnknownItem_throws...` |

> Si un AC no tiene test → la feature **no está terminada**.

---

## Reglas obligatorias (1-5)

1. **No code without an approved spec** (incluye prompts a IA).
2. La spec describe **qué/por qué**, nunca **cómo**.
3. AC **verificables y medibles**.
4. Specs **versionadas en el repo**.
5. Cambio de comportamiento → **Spec-first PR**.

---

## Reglas obligatorias (6-10)

6. Cada PR **referencia su spec**.
7. Specs ambiguas se **rechazan en review**.
8. Tests **derivan de la spec**, no de la implementación.
9. Una spec = **un alcance acotado**.
10. **Glosario de dominio** compartido.

---

## Anti-patrones

- **Spec-as-design** — describe clases/métodos. ❌
- **Spec-washing** — escrita post-hoc. ❌
- **UI-spec** — describe pantallas. ❌
- **Spec sin métricas** — "debe ser rápido". ❌

> En vez de "rápido": **p95 < 200ms con 100 RPS**.

---

## Glosario del dominio

| Término | Definición |
|---|---|
| Customer | Usuario registrado que puede ordenar |
| Restaurant | Establecimiento con menú y horarios |
| MenuItem | Producto vendible de un Restaurant |
| Order | Solicitud de compra con N OrderLines |
| OrderLine | Cantidad + MenuItem + precio |
| Payment | Transacción asociada a Order |
| OperatingHours | Ventanas en que el Restaurant acepta Orders |

---

## Adopción en 4 semanas

- **S1**: piloto con `spec-template.md` en 1 feature.
- **S2**: Spec-first PR en 1 squad.
- **S3**: checklist en PR template global.
- **S4**: retro + métricas + expansión.

**Métricas**: % PRs con spec · bugs por ambigüedad · tiempo de onboarding.

---

<!-- _class: lead -->

# ¿Preguntas?

**Una feature. Una spec. A ver qué pasa.**
Sin ceremonia, sin comité. Solo prueba.
