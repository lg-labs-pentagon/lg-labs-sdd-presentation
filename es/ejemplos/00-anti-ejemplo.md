# Anti-ejemplo: cómo NO escribir una spec

> Este documento muestra el formato típico de un ticket vago que **no** califica como spec SDD.

---

## Ticket FOOD-1234

**Título**: Crear pedido

**Descripción**:
El usuario debe poder crear un pedido con varios items. Validar que todo esté bien y que el restaurante exista. Si hay error mostrar mensaje. Considerar también que no se puedan ordenar items que no existen.

**Asignado a**: equipo backend
**Prioridad**: alta

---

## ¿Por qué este ticket falla como spec?

| Problema | Detalle |
|---|---|
| Sin contexto | No explica el "por qué". ¿Es una nueva línea de negocio? ¿Reemplaza otra cosa? |
| "Todo esté bien" | No es verificable. ¿Qué se valida exactamente? |
| Sin casos borde | ¿Qué pasa si el restaurante está cerrado? ¿Si los ítems son de distintos restaurantes? ¿Stock? |
| Sin glosario | ¿"Pedido" e "Item" significan lo mismo para PM, backend y front? |
| Sin criterios de aceptación | No hay forma de saber cuándo está terminado. |
| Sin out-of-scope | ¿Incluye pago? ¿Notificaciones? ¿Tracking? |
| "Mostrar mensaje" | Mezcla intención con UI. La spec no debe describir UI. |

---

## Consecuencias predecibles

1. **Implementación divergente**: 3 devs harían 3 cosas distintas.
2. **Tests débiles**: solo cubren el happy path.
3. **Bugs en producción** por casos no contemplados.
4. **Rework**: 2-3 PRs adicionales para "completar" la feature.
5. **Discusión en code review** sobre comportamiento que debió definirse antes.

---

> Compara con `01-spec-crear-pedido.md` para ver la versión SDD.
