import { describe, it, expect, beforeEach, vi } from 'vitest';
import { OrderService, totalOf } from './orderService';
import {
  Clock,
  CustomerId,
  MenuItem,
  MenuItemId,
  MenuItemRepository,
  OrderRepository,
  Restaurant,
  RestaurantId,
  RestaurantRepository,
} from './types';

/**
 * Tests para SPEC-ORDER-001 — mapeo 1:1 con Acceptance Criteria.
 */

const CUSTOMER = 'cust-1' as CustomerId;
const R1 = 'rest-1' as RestaurantId;
const R2 = 'rest-2' as RestaurantId;

function activeItem(id: string, restaurantId: RestaurantId, price: number): MenuItem {
  return { id: id as MenuItemId, restaurantId, price, active: true };
}

function openRestaurant(id: RestaurantId): Restaurant {
  return {
    id,
    timezone: 'UTC',
    operatingHours: [{ days: [0, 1, 2, 3, 4, 5, 6], open: '00:00', close: '23:59' }],
  };
}

function closedRestaurant(id: RestaurantId): Restaurant {
  return { id, timezone: 'UTC', operatingHours: [] };
}

describe('OrderService.createOrder — SPEC-ORDER-001', () => {
  let restaurants: RestaurantRepository;
  let menuItems: MenuItemRepository;
  let orders: OrderRepository;
  let clock: Clock;
  let service: OrderService;

  beforeEach(() => {
    restaurants = { findById: vi.fn() };
    menuItems = { findByIds: vi.fn() };
    orders = { save: vi.fn(o => Promise.resolve(o)) };
    clock = { now: () => new Date('2026-05-04T12:00:00Z') };
    service = new OrderService(restaurants, menuItems, orders, clock);
  });

  it('AC-1 + AC-7: items válidos → Order PENDING_PAYMENT con total correcto', async () => {
    vi.mocked(menuItems.findByIds).mockResolvedValue([
      activeItem('item-1', R1, 1000),
      activeItem('item-2', R1, 300),
    ]);
    vi.mocked(restaurants.findById).mockResolvedValue(openRestaurant(R1));

    const result = await service.createOrder(CUSTOMER, [
      { menuItemId: 'item-1' as MenuItemId, quantity: 2 },
      { menuItemId: 'item-2' as MenuItemId, quantity: 1 },
    ]);

    expect(result.ok).toBe(true);
    if (!result.ok) return;
    expect(result.value.status).toBe('PENDING_PAYMENT');
    expect(result.value.restaurantId).toBe(R1);
    expect(totalOf(result.value)).toBe(2300);
  });

  it('AC-2: items de restaurantes distintos → MixedRestaurants', async () => {
    vi.mocked(menuItems.findByIds).mockResolvedValue([
      activeItem('item-1', R1, 1000),
      activeItem('item-2', R2, 500),
    ]);

    const result = await service.createOrder(CUSTOMER, [
      { menuItemId: 'item-1' as MenuItemId, quantity: 1 },
      { menuItemId: 'item-2' as MenuItemId, quantity: 1 },
    ]);

    expect(result.ok).toBe(false);
    if (result.ok) return;
    expect(result.error.kind).toBe('MixedRestaurants');
    expect(orders.save).not.toHaveBeenCalled();
  });

  it('AC-3: restaurante cerrado → RestaurantClosed', async () => {
    vi.mocked(menuItems.findByIds).mockResolvedValue([activeItem('item-1', R1, 1000)]);
    vi.mocked(restaurants.findById).mockResolvedValue(closedRestaurant(R1));

    const result = await service.createOrder(CUSTOMER, [
      { menuItemId: 'item-1' as MenuItemId, quantity: 1 },
    ]);

    expect(result.ok).toBe(false);
    if (result.ok) return;
    expect(result.error.kind).toBe('RestaurantClosed');
    expect(orders.save).not.toHaveBeenCalled();
  });

  it('AC-4: item inexistente → ItemNotFound', async () => {
    vi.mocked(menuItems.findByIds).mockResolvedValue([]);

    const result = await service.createOrder(CUSTOMER, [
      { menuItemId: 'ghost' as MenuItemId, quantity: 1 },
    ]);

    expect(result.ok).toBe(false);
    if (result.ok) return;
    expect(result.error.kind).toBe('ItemNotFound');
  });

  it('AC-5: cantidad 0 → InvalidQuantity', async () => {
    const result = await service.createOrder(CUSTOMER, [
      { menuItemId: 'item-1' as MenuItemId, quantity: 0 },
    ]);

    expect(result.ok).toBe(false);
    if (result.ok) return;
    expect(result.error.kind).toBe('InvalidQuantity');
  });

  it('AC-6: lista vacía → EmptyOrder', async () => {
    const result = await service.createOrder(CUSTOMER, []);
    expect(result.ok).toBe(false);
    if (result.ok) return;
    expect(result.error.kind).toBe('EmptyOrder');
  });
});
