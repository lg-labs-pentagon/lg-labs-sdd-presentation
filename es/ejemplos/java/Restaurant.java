package com.example.orders.domain;

import java.time.*;
import java.util.List;
import java.util.Set;

/**
 * Restaurant — entidad con horarios de operación.
 * Simplificado para SPEC-ORDER-001 (solo lo necesario para validar R2).
 */
public final class Restaurant {

    private final RestaurantId id;
    private final ZoneId timezone;
    private final List<OperatingWindow> operatingHours;

    public Restaurant(RestaurantId id, ZoneId timezone, List<OperatingWindow> operatingHours) {
        this.id = id;
        this.timezone = timezone;
        this.operatingHours = List.copyOf(operatingHours);
    }

    /** R2: el restaurante está abierto en el momento dado. */
    public boolean isOpenAt(Instant instant) {
        ZonedDateTime local = instant.atZone(timezone);
        DayOfWeek day = local.getDayOfWeek();
        LocalTime time = local.toLocalTime();
        return operatingHours.stream()
                .anyMatch(w -> w.contains(day, time));
    }

    public RestaurantId id() { return id; }

    public record OperatingWindow(Set<DayOfWeek> days, LocalTime open, LocalTime close) {
        public boolean contains(DayOfWeek day, LocalTime time) {
            return days.contains(day) && !time.isBefore(open) && time.isBefore(close);
        }
    }
}
