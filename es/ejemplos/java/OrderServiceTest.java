package com.example.orders.application;

import com.example.orders.domain.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para SPEC-ORDER-001.
 *
 * Mapeo 1:1 entre Acceptance Criteria y test methods.
 * Cada test lleva en su nombre el AC que valida.
 */
class OrderServiceTest {

    private RestaurantRepository restaurants;
    private MenuItemRepository menuItems;
    private OrderRepository orders;
    private Clock clock;
    private OrderService service;

    private final RestaurantId R1 = new RestaurantId("rest-1");
    private final RestaurantId R2 = new RestaurantId("rest-2");
    private final CustomerId CUSTOMER = new CustomerId("cust-1");

    @BeforeEach
    void setUp() {
        restaurants = mock(RestaurantRepository.class);
        menuItems = mock(MenuItemRepository.class);
        orders = mock(OrderRepository.class);
        // Lunes 12:00 UTC — dentro del horario por defecto
        clock = Clock.fixed(Instant.parse("2026-05-04T12:00:00Z"), ZoneOffset.UTC);
        service = new OrderService(restaurants, menuItems, orders, clock);

        when(orders.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    /** AC-1 + AC-7 */
    @Test
    void createOrder_withValidItems_returnsOrderInPendingPayment() {
        MenuItem pizza = activeItem("item-1", R1, Money.of(1000));
        MenuItem soda = activeItem("item-2", R1, Money.of(300));
        when(menuItems.findByIds(any())).thenReturn(List.of(pizza, soda));
        when(restaurants.findById(R1)).thenReturn(Optional.of(openRestaurant(R1)));

        Order order = service.createOrder(CUSTOMER, List.of(
                new OrderLineRequest(pizza.id(), 2),
                new OrderLineRequest(soda.id(), 1)
        ));

        assertEquals(Order.Status.PENDING_PAYMENT, order.status());
        assertEquals(R1, order.restaurantId());
        assertEquals(Money.of(2300), order.total()); // AC-7
    }

    /** AC-2 */
    @Test
    void createOrder_withMixedRestaurants_throwsMixedRestaurantsException() {
        MenuItem a = activeItem("item-1", R1, Money.of(1000));
        MenuItem b = activeItem("item-2", R2, Money.of(500));
        when(menuItems.findByIds(any())).thenReturn(List.of(a, b));

        assertThrows(MixedRestaurantsException.class, () ->
                service.createOrder(CUSTOMER, List.of(
                        new OrderLineRequest(a.id(), 1),
                        new OrderLineRequest(b.id(), 1)
                ))
        );
        verify(orders, never()).save(any());
    }

    /** AC-3 */
    @Test
    void createOrder_whenRestaurantClosed_throwsRestaurantClosedException() {
        MenuItem item = activeItem("item-1", R1, Money.of(1000));
        when(menuItems.findByIds(any())).thenReturn(List.of(item));
        when(restaurants.findById(R1)).thenReturn(Optional.of(closedRestaurant(R1)));

        assertThrows(RestaurantClosedException.class, () ->
                service.createOrder(CUSTOMER, List.of(new OrderLineRequest(item.id(), 1)))
        );
        verify(orders, never()).save(any());
    }

    /** AC-4 */
    @Test
    void createOrder_withUnknownItem_throwsItemNotFoundException() {
        when(menuItems.findByIds(any())).thenReturn(List.of()); // ningún item encontrado

        assertThrows(ItemNotFoundException.class, () ->
                service.createOrder(CUSTOMER, List.of(new OrderLineRequest(new MenuItemId("ghost"), 1)))
        );
    }

    /** AC-5 */
    @Test
    void createOrder_withZeroQuantity_throwsInvalidQuantityException() {
        assertThrows(InvalidQuantityException.class, () ->
                service.createOrder(CUSTOMER, List.of(new OrderLineRequest(new MenuItemId("item-1"), 0)))
        );
    }

    /** AC-6 */
    @Test
    void createOrder_withEmptyLines_throwsEmptyOrderException() {
        assertThrows(EmptyOrderException.class, () ->
                service.createOrder(CUSTOMER, List.of())
        );
    }

    // --- helpers ---

    private MenuItem activeItem(String id, RestaurantId restaurantId, Money price) {
        return new MenuItem(new MenuItemId(id), restaurantId, price, true);
    }

    private Restaurant openRestaurant(RestaurantId id) {
        return new Restaurant(id, ZoneOffset.UTC, List.of(
                new Restaurant.OperatingWindow(
                        EnumSet.allOf(DayOfWeek.class),
                        LocalTime.of(0, 0),
                        LocalTime.of(23, 59)
                )
        ));
    }

    private Restaurant closedRestaurant(RestaurantId id) {
        return new Restaurant(id, ZoneOffset.UTC, List.of()); // sin ventanas
    }
}
