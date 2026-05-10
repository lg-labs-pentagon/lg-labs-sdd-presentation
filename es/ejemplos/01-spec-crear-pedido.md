# Spec: Crear Pedido

**ID**: SPEC-ORDER-001
**Versión**: 1.0.0
**Estado**: Approved
**Fecha**: 2026-05-07
**Owners**: Tech Lead Pedidos · PM Marketplace

---

## 1. Context

Los Customers de la plataforma necesitan poder crear pedidos de comida desde el catálogo de un Restaurant. Esta es la primera feature del flujo de compra; bloquea la integración con el módulo de Payment y la entrega.

**Por qué importa**: sin esta capacidad, el resto del marketplace no puede operar. Es el core transaccional del negocio.

**Decisión clave**: un pedido pertenece a **un único Restaurant**. No soportamos pedidos multi-restaurante en esta versión (ver Out of Scope).

---

## 2. Behavior

El sistema permite a un **Customer** autenticado crear un **Order** que contiene una o más **OrderLines**. Una OrderLine referencia un **MenuItem** y una cantidad.

**Reglas de negocio**:

- **R1**: Todos los MenuItems de un Order deben pertenecer al **mismo Restaurant**.
- **R2**: El Order solo puede crearse si el Restaurant está **abierto** según sus `OperatingHours` en el momento de la creación (zona horaria del Restaurant).
- **R3**: Cada MenuItem referenciado debe **existir** y estar **activo** en el catálogo del Restaurant.
- **R4**: La cantidad por OrderLine debe ser un entero **≥ 1**.
- **R5**: El Order debe contener **al menos 1 OrderLine**.
- **R6**: El precio de cada OrderLine se **congela** al precio actual del MenuItem en el momento de la creación.
- **R7**: El Order se crea en estado `PENDING_PAYMENT`.

---

## 3. Acceptance Criteria

| ID | Criterio | Verificable mediante |
|----|----------|----------------------|
| **AC-1** | Dado un Customer, un Restaurant abierto, y N ítems válidos del mismo Restaurant, cuando se crea el pedido, entonces se retorna un Order en estado `PENDING_PAYMENT` con N OrderLines y precios congelados. | Test integración + unit |
| **AC-2** | Dado un Customer y una lista de ítems que pertenecen a **distintos** Restaurants, cuando se intenta crear el pedido, entonces falla con error `MixedRestaurants` y no se persiste nada. | Test unit |
| **AC-3** | Dado un Restaurant **cerrado** (fuera de `OperatingHours`), cuando se intenta crear un pedido, entonces falla con error `RestaurantClosed` y no se persiste nada. | Test unit con clock inyectado |
| **AC-4** | Dado al menos un MenuItem **inexistente** o **inactivo** en la lista, cuando se intenta crear el pedido, entonces falla con error `ItemNotFound(itemId)` indicando el primer ítem problemático y no se persiste nada. | Test unit |
| **AC-5** | Dada una OrderLine con cantidad ≤ 0, cuando se intenta crear el pedido, entonces falla con error `InvalidQuantity` y no se persiste nada. | Test unit |
| **AC-6** | Dada una lista vacía de OrderLines, cuando se intenta crear el pedido, entonces falla con error `EmptyOrder` y no se persiste nada. | Test unit |
| **AC-7** | El precio total del Order es la suma de `(quantity × frozenUnitPrice)` de cada OrderLine. | Test unit |
| **AC-8** | La operación de creación es **atómica**: o se persisten Order + todas las OrderLines, o nada. | Test integración con fallo simulado |

---

## 4. Out of Scope

- Aplicación de cupones o descuentos (spec aparte).
- Gestión de stock / inventario.
- Pedidos multi-restaurante.
- Notificaciones al Restaurant.
- Cálculo de delivery fee.
- Procesamiento de Payment (solo se deja el Order en `PENDING_PAYMENT`).
- UI / endpoints HTTP (este spec es del dominio; el contrato HTTP va en spec separada).

---

## 5. Glossary

| Término | Definición |
|---------|------------|
| **Customer** | Usuario registrado autenticado que puede crear Orders. |
| **Restaurant** | Establecimiento con menú, ubicación y `OperatingHours`. |
| **MenuItem** | Producto vendible perteneciente a un único Restaurant. Tiene precio y flag `active`. |
| **Order** | Solicitud de compra creada por un Customer, contiene 1..N OrderLines y pertenece a un único Restaurant. |
| **OrderLine** | `(menuItemId, quantity, frozenUnitPrice)`. |
| **OperatingHours** | Conjunto de ventanas semanales (ej: Lun 11:00-23:00) en zona horaria del Restaurant. |
| **PENDING_PAYMENT** | Estado inicial del Order tras creación exitosa. |

---

## 6. Open Questions (resueltas antes de Implement)

- [x] ¿Qué pasa si el precio cambió mientras el cliente armaba el carrito? → **Se congela al momento de crear el Order**. Si difiere de lo que vio el cliente, el front debe re-validar antes de llamar.
- [x] ¿Soportamos múltiples Orders concurrentes del mismo Customer? → **Sí**, sin límite en esta versión.

---

## 7. Change Log

| Versión | Fecha | Cambio |
|---------|-------|--------|
| 1.0.0 | 2026-05-07 | Versión inicial aprobada. |
