
package io.aegiscore.audit;

import io.aegiscore.asset.CloudAsset;
import io.aegiscore.asset.repository.CloudAssetRepository;
import io.aegiscore.asset.service.CloudAssetService;
import io.aegiscore.audit.entity.AssetAuditRecordEntity;
import io.aegiscore.audit.repository.AssetAuditRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AssetAuditIntegrationTests {

    @Autowired
    private CloudAssetService cloudAssetService;

    @Autowired
    private CloudAssetRepository assetRepository;

    @Autowired
    private AssetAuditRepository auditRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void shouldPersistAuditOnlyAfterAssetRegistrationCommits() {
        String assetId = "audit-it-" + UUID.randomUUID();

        SecurityContextHolder.clearContext();

        try {
            CloudAsset asset = new CloudAsset(
                    assetId,
                    "Audit integration test",
                    "DATABASE",
                    "AWS",
                    "sa-east-1"
            );

            TransactionTemplate transaction =
                    new TransactionTemplate(transactionManager);

            transaction.executeWithoutResult(status -> {
                cloudAssetService.register(asset);

                assertTrue(
                        assetRepository.existsById(assetId),
                        "Asset should exist inside the transaction"
                );

                assertFalse(
                        auditExistsFor(assetId),
                        "Audit must not be persisted before commit"
                );
            });

            assertTrue(
                    assetRepository.existsById(assetId),
                    "Asset should be committed"
            );

            AssetAuditRecordEntity record = auditRepository.findAll()
                    .stream()
                    .filter(item -> assetId.equals(item.getAssetId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError(
                            "Audit record was not persisted after commit"
                    ));

            assertNotNull(record.getId());
            assertEquals("CREATE", record.getAction());
            assertEquals("unknown", record.getActor());
            assertEquals(assetId, record.getAssetId());
            assertNotNull(record.getOccurredAt());

        } finally {
            auditRepository.findAll()
                    .stream()
                    .filter(item -> assetId.equals(item.getAssetId()))
                    .forEach(auditRepository::delete);

            if (assetRepository.existsById(assetId)) {
                assetRepository.deleteById(assetId);
            }

            SecurityContextHolder.clearContext();
        }
    }

    private boolean auditExistsFor(String assetId) {
        return auditRepository.findAll()
                .stream()
                .anyMatch(item -> assetId.equals(item.getAssetId()));
    }
}
