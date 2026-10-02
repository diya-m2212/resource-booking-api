package com.diya.resource_booking_api.service;

import com.diya.resource_booking_api.model.AppUser;
import com.diya.resource_booking_api.model.Reservation;
import com.diya.resource_booking_api.model.ReservationStatus;
import com.diya.resource_booking_api.model.Resource;
import com.diya.resource_booking_api.repository.ReservationRepository;
import com.diya.resource_booking_api.repository.ResourceRepository;
import com.diya.resource_booking_api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ResourceRepository resourceRepository,
            UserRepository userRepository) {

        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    public Optional<Reservation> createReservation(
            String username,
            Long resourceId,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        if (startTime == null || endTime == null) {
            return Optional.empty();
        }

        if (!endTime.isAfter(startTime)) {
            return Optional.empty();
        }

        Optional<AppUser> userOptional =
                userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<Resource> resourceOptional =
                resourceRepository.findById(resourceId);

        if (resourceOptional.isEmpty()) {
            return Optional.empty();
        }

        Resource resource = resourceOptional.get();

        if (!resource.isAvailable()) {
            return Optional.empty();
        }

        Reservation reservation = new Reservation();

        reservation.setUser(userOptional.get());
        reservation.setResource(resource);
        reservation.setPrice(resource.getPrice());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);

        return Optional.of(
                reservationRepository.save(reservation)
        );
    }

    public Page<Reservation> getReservations(
            String username,
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        Specification<Reservation> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(
                                root.get("user").get("username"),
                                username
                        );

        if (status != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("status"),
                                    status
                            )
            );
        }

        if (minPrice != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("price"),
                                    minPrice
                            )
            );
        }

        if (maxPrice != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("price"),
                                    maxPrice
                            )
            );
        }

        return reservationRepository.findAll(
                specification,
                pageable
        );
    }

    public Page<Reservation> getAllReservations(
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        Specification<Reservation> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        if (status != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("status"),
                                    status
                            )
            );
        }

        if (minPrice != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("price"),
                                    minPrice
                            )
            );
        }

        if (maxPrice != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("price"),
                                    maxPrice
                            )
            );
        }

        return reservationRepository.findAll(
                specification,
                pageable
        );
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Optional<Reservation> updateReservation(
            Long id,
            Long resourceId,
            BigDecimal price,
            ReservationStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            return Optional.empty();
        }

        if (startTime == null || endTime == null) {
            return Optional.empty();
        }

        if (!endTime.isAfter(startTime)) {
            return Optional.empty();
        }

        Optional<Resource> resourceOptional =
                resourceRepository.findById(resourceId);

        if (resourceOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<Reservation> reservationOptional =
                reservationRepository.findById(id);

        if (reservationOptional.isEmpty()) {
            return Optional.empty();
        }

        Reservation reservation = reservationOptional.get();

        reservation.setResource(resourceOptional.get());
        reservation.setPrice(price);
        reservation.setStatus(status);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);

        return Optional.of(
                reservationRepository.save(reservation)
        );
    }

    public Optional<Reservation> updateStatus(
            Long id,
            ReservationStatus status) {

        if (status == null) {
            return Optional.empty();
        }

        return reservationRepository.findById(id)
                .map(reservation -> {

                    reservation.setStatus(status);

                    return reservationRepository.save(reservation);
                });
    }

    public boolean existsById(Long id) {
        return reservationRepository.existsById(id);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}