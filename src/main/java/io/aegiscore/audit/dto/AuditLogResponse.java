package io.aegiscore.audit.dto;

import io.aegiscore.audit.entity.AssetAuditRecordEntity;

import java.time.Instant;

public record AuditLogResponse(
        Long id,
        String action,
        String actor,
        String assetId,
        Instant occurredAt
) {

    public static AuditLogResponse from(
            AssetAuditRecordEntity entity
    ) {
        return new AuditLogResponse(
                entity.getId(),
                entity.getAction(),
                entity.getActor(),
                entity.getAssetId(),
                entity.getOccurredAt()
        );
    }
}
