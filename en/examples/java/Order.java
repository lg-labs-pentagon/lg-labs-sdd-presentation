package com.example.orders.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Order — agregado raíz que representa un pedido de comida.
 *
 * Implementa SPEC-ORDER-001.
 */
public final class Order {

    public enum Status { PENDING_PAYMENT, PAID, CANCELLED }

    private final OrderId id;
    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final List<OrderLine> lines;
    private final Status status;
    private final Instant createdAt;

    public Order(OrderId id,
                 CustomerId customerId,
                 RestaurantId restaurantId,
                 List<OrderLine> lines,
                 Status status,
                 Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);
        this.restaurantId = Objects.requireNonNull(restaurantId);
        this.lines = List.copyOf(lines);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    /** AC-7: total = sum(quantity * frozenUnitPrice). */
    public Money total() {
        return lines.stream()
                .map(OrderLine::subtotal)
                .reduce(Money.ZERO, Money::plus);
    }

    public OrderId id() { return id; }
    public CustomerId customerId() { return customerId; }
    public RestaurantId restaurantId() { return restaurantId; }
    public List<OrderLine> lines() { return lines; }
    public Status status() { return status; }
    public Instant createdAt() { return createdAt; }
}
