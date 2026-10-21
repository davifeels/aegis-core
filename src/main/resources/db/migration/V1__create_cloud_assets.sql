CREATE TABLE cloud_assets (
    id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    provider VARCHAR(20) NOT NULL,
    region VARCHAR(50) NOT NULL,
    CONSTRAINT cloud_assets_pkey PRIMARY KEY (id)
);
