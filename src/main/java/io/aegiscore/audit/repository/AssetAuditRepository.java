package io.aegiscore.audit.repository;

import io.aegiscore.audit.entity.AssetAuditRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AssetAuditRepository
        extends JpaRepository<AssetAuditRecordEntity, Long>,
        JpaSpecificationExecutor<AssetAuditRecordEntity> {
}
