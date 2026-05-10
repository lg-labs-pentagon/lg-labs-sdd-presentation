package com.example.orders.application;

import com.example.orders.domain.*;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OrderService — implementa SPEC-ORDER-001.
 *
 * Cada validación está marcada con el AC que cubre.
 */
public final class OrderService {

    private final RestaurantRepository restaurants;
    private final MenuItemRepository menuItems;
    private final OrderRepository orders;
    private final Clock clock;

    public OrderService(RestaurantRepository restaurants,
                        MenuItemRepository menuItems,
                        OrderRepository orders,
                        Clock clock) {
        this.restaurants = restaurants;
        this.menuItems = menuItems;
        this.orders = orders;
        this.clock = clock;
    }

    public Order createOrder(CustomerId customerId, List<OrderLineRequest> requests) {
        // AC-6: lista vacía
        if (requests == null || requests.isEmpty()) {
            throw new EmptyOrderException();
        }

        // AC-5: cantidad inválida
        for (OrderLineRequest r : requests) {
            if (r.quantity() < 1) {
                throw new InvalidQuantityException(r.menuItemId(), r.quantity());
            }
        }

        // AC-4: items existen y están activos
        Set<MenuItemId> ids = requests.stream()
                .map(OrderLineRequest::menuItemId)
                .collect(Collectors.toSet());
        Map<MenuItemId, MenuItem> found = menuItems.findByIds(ids).stream()
                .collect(Collectors.toMap(MenuItem::id, m -> m));

        for (OrderLineRequest r : requests) {
            MenuItem item = found.get(r.menuItemId());
            if (item == null || !item.active()) {
                throw new ItemNotFoundException(r.menuItemId());
            }
        }

        // AC-2: todos del mismo restaurante
        Set<RestaurantId> restaurantIds = found.values().stream()
                .map(MenuItem::restaurantId)
                .collect(Collectors.toSet());
        if (restaurantIds.size() > 1) {
            throw new MixedRestaurantsException(restaurantIds);
        }
        RestaurantId restaurantId = restaurantIds.iterator().next();

        // AC-3: restaurante abierto
        Restaurant restaurant = restaurants.findById(restaurantId)
                .orElseThrow(() -> new IllegalStateException("Restaurant not found: " + restaurantId));
        Instant now = clock.instant();
        if (!restaurant.isOpenAt(now)) {
            throw new RestaurantClosedException(restaurantId, now);
        }

        // AC-1, AC-7: construir order con precios congelados
        List<OrderLine> lines = requests.stream()
                .map(r -> {
                    MenuItem item = found.get(r.menuItemId());
                    return new OrderLine(item.id(), r.quantity(), item.price());
                })
                .toList();

        Order order = new Order(
                OrderId.generate(),
                customerId,
                restaurantId,
                lines,
                Order.Status.PENDING_PAYMENT,
                now
        );

        // AC-8: persistencia atómica (delegada al adaptador transaccional)
        return orders.save(order);
    }
}
