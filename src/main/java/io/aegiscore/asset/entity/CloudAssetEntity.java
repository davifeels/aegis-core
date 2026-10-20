
package io.aegiscore.asset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cloud_assets")
public class CloudAssetEntity {

    @Id
    @Column(length = 50, nullable = false)
    private String id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 30, nullable = false)
    private String type;

    @Column(length = 20, nullable = false)
    private String provider;

    @Column(length = 50, nullable = false)
    private String region;

    protected CloudAssetEntity() {
    }

    public CloudAssetEntity(
            String id,
            String name,
            String type,
            String provider,
            String region
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.provider = provider;
        this.region = region;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getProvider() {
        return provider;
    }

    public String getRegion() {
        return region;
    }
}
