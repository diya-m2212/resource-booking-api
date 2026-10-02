package com.diya.resource_booking_api.controller;

import com.diya.resource_booking_api.model.ReservationStatus;

public record ReservationStatusRequest(
        ReservationStatus status
) {
}