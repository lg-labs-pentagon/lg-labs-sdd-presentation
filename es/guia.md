# Guía de Presentación — Spec-Driven Development

> Charla de 30 minutos para Senior Developers. Esta guía contiene speaker notes por slide, contexto adicional y respuestas anticipadas a preguntas frecuentes.

---

## Slide 1 — Título + Hook

**Hook (decir literalmente)**:
> "En la era de los agentes de IA, el código se volvió barato. La intención clara, no."

**Notas**:

- Pausa de 2 segundos después del hook.
- Aclara que esta charla no es sobre "más documentación", sino sobre cambiar **dónde** vive la verdad del sistema.

---

## Slide 2 — Objetivos de aprendizaje

Al final de los 30 min, el equipo debería poder:

1. Distinguir SDD de TDD/BDD/DDD y cuándo aplicar cada uno.
2. Reconocer una spec bien escrita vs una mal escrita.
3. Saber las 10 reglas no negociables para adoptar SDD.

**Notas**: pregunta a mano alzada quién ya practica TDD/BDD para calibrar el ritmo.

---

## Slide 3 — Términos base (parte 1)

**Spec**: descripción de **qué** hace un sistema y **por qué**, en términos verificables. No describe el cómo.

**Requerimiento**: necesidad del negocio. Una spec lo formaliza.

**User Story**: formato narrativo (`Como X quiero Y para Z`). Útil para descubrir requerimientos, **insuficiente** como spec.

**Acceptance Criteria (AC)**: condiciones medibles que determinan si la spec se cumple. Son la frontera entre intención y verificación.

**Notas**: la diferencia clave que tienen que llevarse — *user story responde "qué quiere el usuario", spec responde "qué debe hacer el sistema y cómo lo verificamos"*.

---

## Slide 4 — Términos base (parte 2)

**Intención vs Implementación**:

- Intención = qué problema resolvemos
- Implementación = cómo lo resolvemos en código

**Contrato**: acuerdo formal entre componentes (interface, schema, OpenAPI, tipos). Es el puente entre spec y código.

**Notas**: el contrato es el primer artefacto técnico que deriva de una spec. Si tu spec no produce un contrato claro, está mal escrita.

---

## Slide 5 — SDD vs ATDD vs TDD vs BDD vs DDD

| Metodología | Pregunta central | Artefacto principal |
|---|---|---|
| **TDD** | ¿El código funciona? | Test unitario |
| **ATDD** | ¿Cumple los criterios de aceptación del negocio? | Acceptance test |
| **BDD** | ¿El comportamiento es correcto? | Escenario Gherkin |
| **DDD** | ¿El modelo refleja el dominio? | Modelo de dominio |
| **SDD** | ¿Qué debe hacer el sistema y por qué? | **Spec ejecutable** |

**Notas**:

- SDD **no reemplaza** a las otras. Las **orquesta**. La spec es la fuente; los tests, escenarios y modelos derivan de ella.
- **ATDD vs SDD**: ATDD se centra en los acceptance tests como driver del desarrollo (negocio + QA + dev acuerdan tests antes de codificar). SDD es más amplio: la spec contiene los AC, pero también context, behavior rules, glossary y out-of-scope. Los acceptance tests de ATDD pueden verse como **un subproducto** de la spec SDD.
- **ATDD vs BDD**: BDD usa lenguaje natural estructurado (Given/When/Then en Gherkin); ATDD no exige un formato específico. BDD suele considerarse una "implementación" práctica de ATDD.

---

## Slide 6 — Qué es SDD

**Definición operativa**:
> Spec-Driven Development es la práctica de tratar la **especificación** como el artefacto primario y durable del sistema, del cual derivan código, tests, contratos y documentación.

**Implicación clave**: si la spec cambia, el código se regenera o se refactoriza. Si el código cambia sin spec, es un bug en el proceso.

**Notas**: enfatiza "durable". El código se reescribe (frameworks, lenguajes, agentes IA). La spec sobrevive.

---

## Slide 7 — Por qué ahora

- Los agentes de IA generan código a velocidad masiva.
- El cuello de botella ya no es escribir código, es **especificar correctamente la intención**.
- Sin spec clara: el agente alucina, el equipo desalinea, el rework explota.
- SDD es el contrato entre humanos, agentes y sistemas.

**Notas**: cita opcional — Sean Grove ("The New Code") argumenta que las specs serán el principal artefacto que escriban los desarrolladores.

---

## Slide 7b — Vibe-Coding vs SDD

- **Vibe-Coding** — prompt → código → ajusto las *vibes*. Sin contrato, sin verificación.
- **SDD** — intención → spec → código → verificación contra AC.
- **Conviven**: vibes para prototipos desechables; SDD para lo que llega a producción.

**Notas**: el término *vibe-coding* lo popularizó Andrej Karpathy (2025) describiendo el flujo de "aceptar lo que sugiera el LLM y seguir vibrando". Es genial para fines de semana y throwaway code, pero peligroso en producción: sin spec no hay cómo verificar, sin contrato no hay cómo evolucionar. SDD no rechaza la IA — la canaliza con un artefacto durable.

---

## Slide 8 — El ciclo SDD

```
Intent → Spec → Plan → Tasks → Code → Verify
   ↑                                      |
   └──────────────────────────────────────┘
```

Es un loop. La verificación retroalimenta la spec si descubrimos que faltaba un caso.

**Notas**: marca que el loop **no** debe usarse para "spec-washing" (escribir la spec después). Si se descubre algo, se actualiza la spec **antes** de tocar código nuevo.

---

## Slide 9 — Las 5 fases + responsables

| Fase | Output | Responsable principal |
|---|---|---|
| Specify | spec.md | Tech Lead + PM |
| Plan | plan.md (arquitectura, stack, restricciones) | Tech Lead |
| Tasks | tasks.md (descomposición ejecutable) | Tech Lead + Devs |
| Implement | código + tests | Devs (+ agentes IA) |
| Verify | reporte de cobertura de AC | QA + Devs |

**Notas**: en equipos pequeños una persona cubre varios roles, pero **los artefactos siguen siendo distintos**.

---

## Slide 9b — Tasks Layer: la salsa secreta

- Vibe-coding falla porque salta de **Feature → Código** sin pasos intermedios verificables.
- SDD exige **descomposición**: Spec → Plan → **Tasks** → Code.
- Cada task tiene su propio input, output y acceptance criteria.

> **Regla de oro**: cada task debe caber en un diff que un humano revise en menos de 5 minutos.

**Notas**: este es el slide donde se gana o se pierde la audiencia técnica. La capa de Tasks es la diferencia real entre "le pedí a la IA que me lo hiciera" y "trabajé con la IA". Tasks chiquitas → review barato → confianza compuesta. Tasks grandes → review imposible → vuelves a vibe-coding sin querer. Si una task no cabe en 5 min de review, no es una task: es un mini-proyecto, hay que partirla.

---

## Slide 10 — Caso de ejemplo

**Dominio**: sistema de pedidos de comida.

**Entidades**: Customer, Restaurant, MenuItem, Order, OrderLine, Payment, OperatingHours.

**Feature a especificar**:
> "Crear un pedido validando que todos los ítems pertenezcan al mismo restaurante y que el restaurante esté abierto."

**Notas**: caso elegido porque tiene reglas de negocio claras, casos borde y se mapea bien a contratos.

---

## Slide 11 — Anti-ejemplo (cómo NO escribir una spec)

```
Ticket FOOD-1234
Título: Crear pedido
Descripción: El usuario debe poder crear un pedido
con varios items. Validar que todo esté bien.
```

**Problemas**:

- No hay contexto del por qué.
- "Todo esté bien" no es verificable.
- No define casos borde.
- No hay glosario.

**Notas**: este es un ejemplo real que todos hemos visto. Pregunta retórica: *¿cuántos PRs has revisado donde el ticket era así?*

---

## Slide 12 — Spec SDD bien escrita

Estructura mínima:

1. **Context** — por qué existe
2. **Behavior** — qué hace, descrito como reglas
3. **Acceptance Criteria** — verificables, numerados
4. **Out of Scope** — qué NO hace
5. **Glossary** — términos del dominio

Ver `ejemplos/01-spec-crear-pedido.md` para la versión completa.

**Notas**: muestra el archivo en pantalla compartida durante 60 segundos. Que lean en silencio.

---

## Slide 13 — Contrato derivado en Java

```java
public interface OrderService {
  Order createOrder(CustomerId customerId, List<OrderLineRequest> lines);
}
```

Más DTOs, excepciones tipadas (`RestaurantClosedException`, `MixedRestaurantsException`) y tests JUnit que **mapean 1:1** a los AC.

Ver `ejemplos/java/`.

**Notas**: enfatiza que las excepciones tipadas son parte del contrato — no son detalles de implementación.

---

## Slide 14 — Contrato derivado en TypeScript

```ts
export interface OrderService {
  createOrder(customerId: CustomerId, lines: OrderLineRequest[]): Promise<Order>;
}
```

Tipos discriminados para errores (`Result<Order, OrderError>`), tests Vitest con el mismo mapeo a AC.

Ver `ejemplos/typescript/`.

**Notas**: contrasta el manejo de errores idiomático en cada lenguaje. La spec es la misma; el contrato cambia ligeramente.

---

## Slide 15 — Verificación: AC ↔ Tests

Tabla de mapeo:

| AC | Test |
|---|---|
| AC-1: pedido válido se crea | `createOrder_withValidItems_returnsOrder` |
| AC-2: rechaza items de distintos restaurantes | `createOrder_withMixedRestaurants_throwsMixedRestaurantsException` |
| AC-3: rechaza si restaurante cerrado | `createOrder_whenRestaurantClosed_throwsRestaurantClosedException` |
| AC-4: rechaza items inexistentes | `createOrder_withUnknownItem_throwsItemNotFoundException` |

**Regla**: si un AC no tiene test, la feature no está terminada.

**Notas**: sugiere tools que automatizan este mapeo (links en código, anotaciones, o naming convention).

---

## Slide 16 — Reglas obligatorias (1-5)

1. **No code without an approved spec** (incluye prompts a IA).
2. La spec describe **qué/por qué**, nunca **cómo**.
3. Criterios de aceptación **verificables y medibles**.
4. Specs **versionadas en el repo**, junto al código que cumplen.
5. Cambio de comportamiento → **Spec-first PR** antes que el code-PR.

**Notas**: la regla 5 es la que más resistencia genera. Anticipa: "agrega un PR extra"; respuesta: "evita 3 PRs de rework".

---

## Slide 17 — Reglas obligatorias (6-10)

6. Cada PR **referencia su spec** (link explícito).
7. Specs ambiguas se **rechazan en review** igual que código malo.
8. Tests **derivan de la spec**, no de la implementación.
9. Una spec = **un alcance acotado** (regla de tamaño: cabe en una pantalla).
10. **Glosario de dominio** compartido y mantenido.

**Notas**: la 8 es sutil pero crítica. Si escribes el test después del código, estás validando lo que hiciste, no lo que debías hacer.

---

## Slide 18 — Anti-patrones

- **Spec-as-design**: la spec describe la implementación (clases, métodos). Mal.
- **Spec-washing**: escribirla después del código para cumplir el proceso. Inútil.
- **UI-spec**: describe pantallas en vez de comportamiento. Frágil.
- **Spec sin métricas**: "debe ser rápido" en vez de "p95 < 200ms".

**Notas**: pide ejemplos al equipo en voz alta. Garantizado que aparecen los 4.

---

## Slide 19 — Glosario del dominio (slide de cierre conceptual)

| Término | Definición |
|---|---|
| Customer | Usuario registrado que puede ordenar |
| Restaurant | Establecimiento con menú y horarios |
| MenuItem | Producto vendible perteneciente a un Restaurant |
| Order | Solicitud de compra de un Customer con N OrderLines |
| OrderLine | Cantidad + MenuItem + precio en momento de orden |
| Payment | Transacción asociada a una Order |
| OperatingHours | Ventanas de tiempo en que un Restaurant acepta Orders |

**Notas**: el glosario es parte de la spec. Sin él, dos developers entienden cosas distintas por "pedido".

---

## Slide 20 — Adopción 4 semanas + Q&A

**Semana 1**: adoptar `spec-template.md` para 1 feature piloto.
**Semana 2**: requerir Spec-first PR en 1 squad.
**Semana 3**: añadir checklist al PR template global.
**Semana 4**: retrospectiva, métricas (rework, claridad), expansión.

**Métricas de éxito**:

- % PRs con spec referenciada
- Bugs por ambigüedad de requerimiento (debería bajar)
- Tiempo de onboarding a nuevas features

**Cierre**: "Una feature. Una spec. A ver qué pasa. Sin ceremonia, sin comité. Solo prueba."

---

## Preguntas frecuentes (preparadas)

**P: ¿Esto no es waterfall disfrazado?**
R: No. La spec es viva, versionada, refactorizable. Lo que NO hacemos es escribir código primero y documentar después.

**P: ¿No retrasa el desarrollo?**
R: A corto plazo, sí (10-20%). A mediano plazo, reduce rework 30-50% según estudios internos típicos. Mide en tu equipo.

**P: ¿Quién escribe la spec?**
R: Tech lead + PM en colaboración. El dev senior puede escribirla solo si el dominio es claro.

**P: ¿Qué hago con el código legacy sin spec?**
R: No reescribas. Cuando toques una zona, escribe la spec del **comportamiento actual** primero (spec retroactiva), luego cambia.

**P: ¿Cómo encajan los agentes de IA?**
R: La spec es el prompt durable. Los agentes generan código a partir de ella. Sin spec, los agentes alucinan.
