package com.diya.resource_booking_api.service;

import com.diya.resource_booking_api.model.Resource;
import com.diya.resource_booking_api.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Optional<Resource> updateResource(
            Long id,
            Resource updatedResource) {

        return resourceRepository.findById(id)
                .map(resource -> {
                    resource.setName(updatedResource.getName());
                    resource.setDescription(updatedResource.getDescription());
                    resource.setPrice(updatedResource.getPrice());
                    resource.setAvailable(updatedResource.isAvailable());

                    return resourceRepository.save(resource);
                });
    }

    public boolean existsById(Long id) {
        return resourceRepository.existsById(id);
    }

    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }
}
