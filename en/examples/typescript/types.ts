/**
 * Tipos del dominio para SPEC-ORDER-001.
 */

export type CustomerId = string & { readonly __brand: 'CustomerId' };
export type RestaurantId = string & { readonly __brand: 'RestaurantId' };
export type MenuItemId = string & { readonly __brand: 'MenuItemId' };
export type OrderId = string & { readonly __brand: 'OrderId' };

export type Money = number; // céntimos

export interface MenuItem {
  id: MenuItemId;
  restaurantId: RestaurantId;
  price: Money;
  active: boolean;
}

export interface OperatingWindow {
  /** 0 = domingo, 6 = sábado */
  days: number[];
  /** "HH:mm" */
  open: string;
  close: string;
}

export interface Restaurant {
  id: RestaurantId;
  timezone: string; // IANA, ej "America/Bogota"
  operatingHours: OperatingWindow[];
}

export interface OrderLineRequest {
  menuItemId: MenuItemId;
  quantity: number;
}

export interface OrderLine {
  menuItemId: MenuItemId;
  quantity: number;
  frozenUnitPrice: Money;
}

export type OrderStatus = 'PENDING_PAYMENT' | 'PAID' | 'CANCELLED';

export interface Order {
  id: OrderId;
  customerId: CustomerId;
  restaurantId: RestaurantId;
  lines: OrderLine[];
  status: OrderStatus;
  createdAt: Date;
}

/** Errores tipados — parte del contrato (AC-2..AC-6). */
export type OrderError =
  | { kind: 'EmptyOrder' }
  | { kind: 'InvalidQuantity'; menuItemId: MenuItemId; quantity: number }
  | { kind: 'ItemNotFound'; menuItemId: MenuItemId }
  | { kind: 'MixedRestaurants'; restaurantIds: RestaurantId[] }
  | { kind: 'RestaurantClosed'; restaurantId: RestaurantId; at: Date };

export type Result<T, E> =
  | { ok: true; value: T }
  | { ok: false; error: E };

/** Puertos */
export interface RestaurantRepository {
  findById(id: RestaurantId): Promise<Restaurant | null>;
}
export interface MenuItemRepository {
  findByIds(ids: MenuItemId[]): Promise<MenuItem[]>;
}
export interface OrderRepository {
  save(order: Order): Promise<Order>;
}
export interface Clock {
  now(): Date;
}
