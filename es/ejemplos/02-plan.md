# Plan de Implementación — SPEC-ORDER-001

> Deriva de `01-spec-crear-pedido.md`. Define **cómo** se construye, sin cambiar el **qué**.

---

## Arquitectura

- **Capa de dominio**: entidades puras (`Order`, `OrderLine`, `Restaurant`, `MenuItem`) sin dependencias externas.
- **Servicio de aplicación**: `OrderService` orquesta validaciones y persistencia.
- **Puertos**:
  - `RestaurantRepository.findById(id) → Optional<Restaurant>`
  - `MenuItemRepository.findByIds(ids) → List<MenuItem>`
  - `OrderRepository.save(order) → Order`
  - `Clock.now() → Instant`
- **Adaptadores**: implementaciones concretas (DB, in-memory para tests).

## Stack y restricciones

- **Java**: 21, JUnit 5, Mockito.
- **TypeScript**: Node 20, Vitest, sin frameworks pesados.
- Sin acceso a red en el dominio.
- Clock inyectado (no `Instant.now()` directo en código de dominio).
- Operación transaccional (manejo en adaptador, contrato lo expone vía `OrderRepository.save`).

## Decisiones técnicas

| Decisión | Justificación |
|----------|---------------|
| Excepciones tipadas (Java) / `Result<T,E>` (TS) | Errores son parte del contrato, no efectos secundarios. |
| Precio congelado en `OrderLine` | Cumple R6, evita inconsistencias futuras. |
| Validar items existen antes que restaurante abierto | Mensaje de error más útil al usuario. |
| Detener en primer item inválido | Simplicidad; iteraciones futuras pueden retornar lista. |

## Riesgos

- **Concurrencia en stock**: fuera de scope, pero futura spec deberá considerar locks o versionado.
- **Zonas horarias**: `Restaurant` debe almacenar IANA tz; la comparación de horario usa esa tz, no UTC.
