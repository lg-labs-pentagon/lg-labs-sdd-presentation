package com.example.orders.domain;

import java.util.*;

/**
 * Tipos de soporte para SPEC-ORDER-001.
 * Agrupados aquí por brevedad — en producción cada uno iría en su propio archivo.
 */

public final class DomainTypes {
    private DomainTypes() {}
}

// --- Identifiers (value objects) ---
record CustomerId(String value) {}
record RestaurantId(String value) {}
record MenuItemId(String value) {}
record OrderId(String value) {
    static OrderId generate() { return new OrderId(UUID.randomUUID().toString()); }
}

// --- Money ---
final class Money {
    static final Money ZERO = new Money(0);
    private final long cents;
    private Money(long cents) { this.cents = cents; }
    static Money of(long cents) { return new Money(cents); }
    Money plus(Money other) { return new Money(this.cents + other.cents); }
    Money times(int n) { return new Money(this.cents * n); }
    long cents() { return cents; }
    @Override public boolean equals(Object o) {
        return o instanceof Money m && m.cents == cents;
    }
    @Override public int hashCode() { return Long.hashCode(cents); }
}

// --- Entities ---
record MenuItem(MenuItemId id, RestaurantId restaurantId, Money price, boolean active) {}

record OrderLine(MenuItemId menuItemId, int quantity, Money frozenUnitPrice) {
    Money subtotal() { return frozenUnitPrice.times(quantity); }
}

// --- Request DTOs ---
record OrderLineRequest(MenuItemId menuItemId, int quantity) {}

// --- Ports ---
interface RestaurantRepository {
    Optional<Restaurant> findById(RestaurantId id);
}
interface MenuItemRepository {
    List<MenuItem> findByIds(Set<MenuItemId> ids);
}
interface OrderRepository {
    Order save(Order order);
}

// --- Domain exceptions (AC-2..AC-6) ---
class EmptyOrderException extends RuntimeException {
    EmptyOrderException() { super("Order must contain at least one line"); }
}
class InvalidQuantityException extends RuntimeException {
    InvalidQuantityException(MenuItemId id, int qty) {
        super("Invalid quantity " + qty + " for item " + id.value());
    }
}
class ItemNotFoundException extends RuntimeException {
    ItemNotFoundException(MenuItemId id) { super("Item not found or inactive: " + id.value()); }
}
class MixedRestaurantsException extends RuntimeException {
    MixedRestaurantsException(Set<RestaurantId> ids) {
        super("Order spans multiple restaurants: " + ids);
    }
}
class RestaurantClosedException extends RuntimeException {
    RestaurantClosedException(RestaurantId id, java.time.Instant at) {
        super("Restaurant " + id.value() + " is closed at " + at);
    }
}
