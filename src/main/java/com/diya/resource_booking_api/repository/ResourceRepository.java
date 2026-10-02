package com.diya.resource_booking_api.repository;

import com.diya.resource_booking_api.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}