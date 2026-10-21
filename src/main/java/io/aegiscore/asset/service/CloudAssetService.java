package io.aegiscore.asset.service;

import io.aegiscore.asset.CloudAsset;
import io.aegiscore.asset.entity.CloudAssetEntity;
import io.aegiscore.asset.repository.CloudAssetRepository;
import io.aegiscore.audit.AssetAuditEvent;
import io.aegiscore.exception.ResourceConflictException;
import io.aegiscore.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CloudAssetService {

    private final CloudAssetRepository assetRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CloudAssetService(
            CloudAssetRepository assetRepository,
            ApplicationEventPublisher eventPublisher) {
        this.assetRepository = assetRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CloudAsset register(CloudAsset asset) {
        if (assetRepository.existsById(asset.id())) {
            throw new ResourceConflictException(
                    "Asset already exists: " + asset.id()
            );
        }

        CloudAssetEntity entity = new CloudAssetEntity(
                asset.id(),
                asset.name(),
                asset.type(),
                asset.provider(),
                asset.region()
        );

        try {
            CloudAssetEntity saved = assetRepository.saveAndFlush(entity);

            publishAuditEvent("CREATE", saved.getId());

            return toDto(saved);
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceConflictException(
                    "Asset could not be registered because of a data integrity conflict"
            );
        }
    }

    @Transactional(readOnly = true)
    public List<CloudAsset> findAll() {
        return assetRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CloudAsset findById(String id) {
        CloudAssetEntity entity = assetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Asset not found: " + id
                        ));

        return toDto(entity);
    }

    @Transactional
    public CloudAsset update(String id, CloudAsset asset) {
        CloudAssetEntity existing = assetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Asset not found: " + id
                        ));

        if (!id.equals(asset.id())) {
            throw new IllegalArgumentException(
                    "Asset ID in the URL must match the asset ID in the body"
            );
        }

        CloudAssetEntity updated = new CloudAssetEntity(
                existing.getId(),
                asset.name(),
                asset.type(),
                asset.provider(),
                asset.region()
        );

        CloudAssetEntity saved = assetRepository.save(updated);

        publishAuditEvent("UPDATE", saved.getId());

        return toDto(saved);
    }

    @Transactional
    public void deleteById(String id) {
        CloudAssetEntity entity = assetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Asset not found: " + id
                        ));

        assetRepository.delete(entity);

        publishAuditEvent("DELETE", entity.getId());
    }

    private void publishAuditEvent(String action, String assetId) {
        eventPublisher.publishEvent(
                new AssetAuditEvent(action, currentActor(), assetId)
        );
    }

    private String currentActor() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            return "unknown";
        }

        return authentication.getName();
    }

    private CloudAsset toDto(CloudAssetEntity entity) {
        return new CloudAsset(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getProvider(),
                entity.getRegion()
        );
    }
}
