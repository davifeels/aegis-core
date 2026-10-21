
package io.aegiscore.audit;

import io.aegiscore.audit.service.AssetAuditPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AssetAuditListener {

    private static final Logger log =
            LoggerFactory.getLogger(AssetAuditListener.class);

    private final AssetAuditPersistenceService persistenceService;

    public AssetAuditListener(
            AssetAuditPersistenceService persistenceService
    ) {
        this.persistenceService = persistenceService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AssetAuditEvent event) {
        String action = safeField(event.action(), 20);
        String actor = safeField(event.actor(), 100);
        String assetId = safeField(event.assetId(), 50);

        log.info(
                "AUDIT action={} actor={} assetId={}",
                action,
                actor,
                assetId
        );

        try {
            persistenceService.record(action, actor, assetId);
        } catch (RuntimeException exception) {
            log.error(
                    "AUDIT_PERSISTENCE_FAILURE action={} actor={} assetId={}",
                    action,
                    actor,
                    assetId,
                    exception
            );
        }
    }

    private String safeField(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        String sanitized = value.replaceAll("[\\p{Cntrl}]", "_");

        return sanitized.length() > maxLength
                ? sanitized.substring(0, maxLength)
                : sanitized;
    }
}
