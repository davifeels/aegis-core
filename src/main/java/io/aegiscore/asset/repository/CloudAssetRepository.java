package io.aegiscore.asset.repository;

import io.aegiscore.asset.entity.CloudAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudAssetRepository
        extends JpaRepository<CloudAssetEntity, String> {
}
