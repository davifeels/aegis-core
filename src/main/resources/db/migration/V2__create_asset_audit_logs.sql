CREATE TABLE asset_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(20) NOT NULL,
    actor VARCHAR(100) NOT NULL,
    asset_id VARCHAR(50) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_asset_audit_logs_asset_id
    ON asset_audit_logs (asset_id);

CREATE INDEX idx_asset_audit_logs_occurred_at
    ON asset_audit_logs (occurred_at);
