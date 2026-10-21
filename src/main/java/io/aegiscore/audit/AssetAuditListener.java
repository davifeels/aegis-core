package io.aegiscore.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AssetAuditListener {

    private static final Logger log =
            LoggerFactory.getLogger(AssetAuditListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AssetAuditEvent event) {
        log.info(
                "AUDIT action={} actor={} assetId={}",
                safeField(event.action()),
                safeField(event.actor()),
                safeField(event.assetId())
        );
    }

    private String safeField(String value) {
        if (value == null) {
            return "unknown";
        }

        String sanitized = value.replaceAll("[\\p{Cntrl}]", "_");

        return sanitized.length() > 100
                ? sanitized.substring(0, 100)
                : sanitized;
    }
}
