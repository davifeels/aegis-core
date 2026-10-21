package io.aegiscore.audit;

public record AssetAuditEvent(
        String action,
        String actor,
        String assetId
) {
}
