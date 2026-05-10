import {
  Clock,
  CustomerId,
  MenuItem,
  MenuItemId,
  MenuItemRepository,
  Order,
  OrderError,
  OrderId,
  OrderLine,
  OrderLineRequest,
  OrderRepository,
  Restaurant,
  RestaurantId,
  RestaurantRepository,
  Result,
} from './types';

/**
 * createOrder — implementa SPEC-ORDER-001.
 * Cada validación referencia el AC correspondiente.
 */
export class OrderService {
  constructor(
    private readonly restaurants: RestaurantRepository,
    private readonly menuItems: MenuItemRepository,
    private readonly orders: OrderRepository,
    private readonly clock: Clock,
  ) {}

  async createOrder(
    customerId: CustomerId,
    requests: OrderLineRequest[],
  ): Promise<Result<Order, OrderError>> {
    // AC-6
    if (!requests || requests.length === 0) {
      return err({ kind: 'EmptyOrder' });
    }

    // AC-5
    for (const r of requests) {
      if (r.quantity < 1) {
        return err({ kind: 'InvalidQuantity', menuItemId: r.menuItemId, quantity: r.quantity });
      }
    }

    // AC-4
    const ids = [...new Set(requests.map(r => r.menuItemId))];
    const found = await this.menuItems.findByIds(ids);
    const byId = new Map(found.map(m => [m.id, m]));
    for (const r of requests) {
      const item = byId.get(r.menuItemId);
      if (!item || !item.active) {
        return err({ kind: 'ItemNotFound', menuItemId: r.menuItemId });
      }
    }

    // AC-2
    const restaurantIds = [...new Set(found.map(m => m.restaurantId))];
    if (restaurantIds.length > 1) {
      return err({ kind: 'MixedRestaurants', restaurantIds });
    }
    const restaurantId = restaurantIds[0];

    // AC-3
    const restaurant = await this.restaurants.findById(restaurantId);
    if (!restaurant) {
      throw new Error(`Restaurant not found: ${restaurantId}`);
    }
    const now = this.clock.now();
    if (!isOpenAt(restaurant, now)) {
      return err({ kind: 'RestaurantClosed', restaurantId, at: now });
    }

    // AC-1, AC-7
    const lines: OrderLine[] = requests.map(r => {
      const item = byId.get(r.menuItemId)!;
      return {
        menuItemId: item.id,
        quantity: r.quantity,
        frozenUnitPrice: item.price,
      };
    });

    const order: Order = {
      id: generateOrderId(),
      customerId,
      restaurantId,
      lines,
      status: 'PENDING_PAYMENT',
      createdAt: now,
    };

    // AC-8: atomicidad delegada al repositorio.
    const saved = await this.orders.save(order);
    return ok(saved);
  }
}

/** AC-7 helper. */
export function totalOf(order: Order): number {
  return order.lines.reduce((sum, l) => sum + l.quantity * l.frozenUnitPrice, 0);
}

function isOpenAt(restaurant: Restaurant, at: Date): boolean {
  const local = new Date(at.toLocaleString('en-US', { timeZone: restaurant.timezone }));
  const day = local.getDay();
  const hhmm = `${pad(local.getHours())}:${pad(local.getMinutes())}`;
  return restaurant.operatingHours.some(
    w => w.days.includes(day) && hhmm >= w.open && hhmm < w.close,
  );
}

function pad(n: number): string {
  return n.toString().padStart(2, '0');
}

function generateOrderId(): OrderId {
  return crypto.randomUUID() as OrderId;
}

function ok<T, E>(value: T): Result<T, E> {
  return { ok: true, value };
}

function err<T, E>(error: E): Result<T, E> {
  return { ok: false, error };
}
