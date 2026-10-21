
package io.aegiscore.audit.service;

import io.aegiscore.audit.entity.AssetAuditRecordEntity;
import io.aegiscore.audit.repository.AssetAuditRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AssetAuditPersistenceServiceTests {

    @Autowired
    private AssetAuditPersistenceService persistenceService;

    @Autowired
    private AssetAuditRepository auditRepository;

    @Test
    @Transactional
    void shouldPersistAuditRecordWithExpectedValues() {
        String action = "CREATE";
        String actor = "test-user";
        String assetId = "asset-audit-integration-001";

        Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);

        persistenceService.record(action, actor, assetId);

        Instant after = Instant.now().plus(1, ChronoUnit.SECONDS);

        AssetAuditRecordEntity record = auditRepository
                .findAll()
                .stream()
                .filter(item -> assetId.equals(item.getAssetId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Audit record was not persisted"
                ));

        assertNotNull(record.getId());
        assertEquals(action, record.getAction());
        assertEquals(actor, record.getActor());
        assertEquals(assetId, record.getAssetId());
        assertNotNull(record.getOccurredAt());
        assertTrue(
                !record.getOccurredAt().isBefore(before)
                        && !record.getOccurredAt().isAfter(after),
                "Audit timestamp should be close to the persistence time"
        );
    }
}
