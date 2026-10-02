package com.diya.resource_booking_api.controller;

import com.diya.resource_booking_api.model.ReservationStatus;
import com.diya.resource_booking_api.service.ReservationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestParam Long resourceId,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime,
            Authentication authentication) {

        if (startTime == null || endTime == null) {
            return ResponseEntity.badRequest()
                    .body("Start time and end time are required");
        }

        if (!endTime.isAfter(startTime)) {
            return ResponseEntity.badRequest()
                    .body("End time must be after start time");
        }

        String username = authentication.getName();

        return reservationService.createReservation(
                        username,
                        resourceId,
                        startTime,
                        endTime
                )
                .<ResponseEntity<?>>map(reservation ->
                        ResponseEntity.ok(
                                ReservationResponse.from(reservation)
                        )
                )
                .orElse(
                        ResponseEntity.badRequest()
                                .body("Unable to create reservation")
                );
    }

    @GetMapping
    public ResponseEntity<?> getMyReservations(
            Authentication authentication,
            @RequestParam(required = false)
            ReservationStatus status,
            @RequestParam(required = false)
            BigDecimal minPrice,
            @RequestParam(required = false)
            BigDecimal maxPrice,
            Pageable pageable) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                reservationService
                        .getReservations(
                                username,
                                status,
                                minPrice,
                                maxPrice,
                                pageable
                        )
                        .map(ReservationResponse::from)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllReservations(
            Authentication authentication,
            @RequestParam(required = false)
            ReservationStatus status,
            @RequestParam(required = false)
            BigDecimal minPrice,
            @RequestParam(required = false)
            BigDecimal maxPrice,
            Pageable pageable) {

        if (!isAdmin(authentication)) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access required");
        }

        return ResponseEntity.ok(
                reservationService
                        .getAllReservations(
                                status,
                                minPrice,
                                maxPrice,
                                pageable
                        )
                        .map(ReservationResponse::from)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(
            @PathVariable Long id,
            Authentication authentication) {

        return reservationService
                .getReservationById(id)
                .<ResponseEntity<?>>map(reservation -> {

                    if (isAdmin(authentication)) {
                        return ResponseEntity.ok(
                                ReservationResponse.from(reservation)
                        );
                    }

                    String username = authentication.getName();

                    if (!reservation.getUser()
                            .getUsername()
                            .equals(username)) {

                        return ResponseEntity
                                .status(403)
                                .body(
                                        "You can only view your own reservations"
                                );
                    }

                    return ResponseEntity.ok(
                            ReservationResponse.from(reservation)
                    );
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(
            @PathVariable Long id,
            @RequestParam Long resourceId,
            @RequestParam BigDecimal price,
            @RequestParam ReservationStatus status,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime,
            Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access required");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.badRequest()
                    .body("Price cannot be negative");
        }

        if (startTime == null || endTime == null) {
            return ResponseEntity.badRequest()
                    .body("Start time and end time are required");
        }

        if (!endTime.isAfter(startTime)) {
            return ResponseEntity.badRequest()
                    .body("End time must be after start time");
        }

        return reservationService
                .updateReservation(
                        id,
                        resourceId,
                        price,
                        status,
                        startTime,
                        endTime
                )
                .<ResponseEntity<?>>map(reservation ->
                        ResponseEntity.ok(
                                ReservationResponse.from(reservation)
                        )
                )
                .orElse(
                        ResponseEntity.badRequest()
                                .body("Unable to update reservation")
                );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReservationStatus(
            @PathVariable Long id,
            @RequestBody ReservationStatusRequest request,
            Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access required");
        }

        if (request.status() == null) {
            return ResponseEntity.badRequest()
                    .body("Status is required");
        }

        return reservationService
                .updateStatus(id, request.status())
                .<ResponseEntity<?>>map(reservation ->
                        ResponseEntity.ok(
                                ReservationResponse.from(reservation)
                        )
                )
                .orElse(
                        ResponseEntity.badRequest()
                                .body("Unable to update status")
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(
            @PathVariable Long id,
            Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity
                    .status(403)
                    .body("Admin access required");
        }

        if (!reservationService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN"));
    }
}