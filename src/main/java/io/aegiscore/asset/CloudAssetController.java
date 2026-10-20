package io.aegiscore.asset;

import io.aegiscore.asset.service.CloudAssetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
public class CloudAssetController {

    private final CloudAssetService assetService;

    public CloudAssetController(CloudAssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<CloudAsset> register(
            @Valid @RequestBody CloudAsset asset) {

        CloudAsset createdAsset = assetService.register(asset);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAsset);
    }

    @GetMapping
    public List<CloudAsset> findAll() {
        return assetService.findAll();
    }

    @GetMapping("/{id}")
    public CloudAsset findById(@PathVariable String id) {
        return assetService.findById(id);
    }

    @PutMapping("/{id}")
    public CloudAsset update(
            @PathVariable String id,
            @Valid @RequestBody CloudAsset asset) {

        return assetService.update(id, asset);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        assetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
