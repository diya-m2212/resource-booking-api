package com.diya.resource_booking_api.controller;

import com.diya.resource_booking_api.model.Reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        String username,
        Long resourceId,
        String resourceName,
        BigDecimal price,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime
) {

    public static ReservationResponse from(Reservation reservation) {

        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getUsername(),
                reservation.getResource().getId(),
                reservation.getResource().getName(),
                reservation.getPrice(),
                reservation.getStatus().name(),
                reservation.getStartTime(),
                reservation.getEndTime()
        );
    }
}