
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void shouldPersistAuditAfterAssetRegistrationCommits() {
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

            cloudAssetService.register(asset);

            assertTrue(
                    assetRepository.existsById(assetId),
                    "Asset should be committed before the service returns"
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

            assetRepository.deleteById(assetId);
            SecurityContextHolder.clearContext();
        }
    }
}
