package com.diya.resource_booking_api.controller;

import com.diya.resource_booking_api.model.Resource;
import com.diya.resource_booking_api.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    // GET /resources
    // ADMIN and USER can view resources
    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    // GET /resources/{id}
    // ADMIN and USER can view a resource
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(
            @PathVariable Long id) {

        return resourceService.getResourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /resources
    // ADMIN only
    @PostMapping
    public ResponseEntity<?> createResource(
            @RequestBody @Valid Resource resource,
            Authentication authentication) {

        if (!hasAdminRole(authentication)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                resourceService.createResource(resource)
        );
    }

    // PUT /resources/{id}
    // ADMIN only
    @PutMapping("/{id}")
    public ResponseEntity<?> updateResource(
            @PathVariable Long id,
            @RequestBody @Valid Resource updatedResource,
            Authentication authentication) {

        if (!hasAdminRole(authentication)) {
            return ResponseEntity.status(403).build();
        }

        return resourceService.updateResource(id, updatedResource)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /resources/{id}
    // ADMIN only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long id,
            Authentication authentication) {

        if (!hasAdminRole(authentication)) {
            return ResponseEntity.status(403).build();
        }

        if (!resourceService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        resourceService.deleteResource(id);

        return ResponseEntity.noContent().build();
    }

    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));
    }
}