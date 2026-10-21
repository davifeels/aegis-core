package io.aegiscore.audit.repository;

import io.aegiscore.audit.entity.AssetAuditRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetAuditRepository
        extends JpaRepository<AssetAuditRecordEntity, Long> {
}
