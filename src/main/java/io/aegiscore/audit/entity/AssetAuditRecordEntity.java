package io.aegiscore.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "asset_audit_logs")
public class AssetAuditRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action", length = 20, nullable = false)
    private String action;

    @Column(name = "actor", length = 100, nullable = false)
    private String actor;

    @Column(name = "asset_id", length = 50, nullable = false)
    private String assetId;

    @Column(name = "occurred_at", nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant occurredAt;

    protected AssetAuditRecordEntity() {
    }

    public AssetAuditRecordEntity(
            String action,
            String actor,
            String assetId
    ) {
        this.action = action;
        this.actor = actor;
        this.assetId = assetId;
        this.occurredAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getActor() {
        return actor;
    }

    public String getAssetId() {
        return assetId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
