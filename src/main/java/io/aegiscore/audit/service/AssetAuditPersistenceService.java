package io.aegiscore.audit.service;

import io.aegiscore.audit.entity.AssetAuditRecordEntity;
import io.aegiscore.audit.repository.AssetAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetAuditPersistenceService {

    private final AssetAuditRepository auditRepository;

    public AssetAuditPersistenceService(
            AssetAuditRepository auditRepository
    ) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String action, String actor, String assetId) {
        auditRepository.saveAndFlush(
                new AssetAuditRecordEntity(action, actor, assetId)
        );
    }
}
