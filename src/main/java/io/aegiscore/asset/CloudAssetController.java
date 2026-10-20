
package io.aegiscore.asset;

import io.aegiscore.asset.service.CloudAssetService;
import io.aegiscore.exception.ApiErrorResponse;
import io.aegiscore.exception.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/assets")
@Tag(name = "Cloud Assets", description = "Manage cloud assets")
public class CloudAssetController {

    private final CloudAssetService assetService;

    public CloudAssetController(CloudAssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    @Operation(
            summary = "Register a cloud asset",
            description = "Registers a new cloud asset after validating the request."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Cloud asset created successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CloudAsset.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid fields, malformed JSON or invalid request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            oneOf = {
                                    ValidationErrorResponse.class,
                                    ApiErrorResponse.class
                            }
                    )
            )
    )
    @ApiResponse(
            responseCode = "409",
            description = "A conflict prevents the asset from being registered",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "status": 409,
                              "error": "Resource conflict",
                              "message": "Asset already exists: asset-001"
                            }
                            """)
            )
    )
    public ResponseEntity<CloudAsset> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Cloud asset information",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CloudAsset.class),
                            examples = @ExampleObject(
                                    name = "AWS Database",
                                    summary = "Example cloud database asset",
                                    value = """
                                            {
                                              "id": "asset-001",
                                              "name": "Production Database",
                                              "type": "DATABASE",
                                              "provider": "AWS",
                                              "region": "us-east-1"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody CloudAsset asset) {

        CloudAsset createdAsset = assetService.register(asset);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAsset);
    }

    @GetMapping
    @Operation(
            summary = "List cloud assets",
            description = "Returns the registered cloud assets."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cloud assets retrieved successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            type = "array",
                            implementation = CloudAsset.class
                    )
            )
    )
    public List<CloudAsset> findAll() {
        return assetService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a cloud asset",
            description = "Retrieves a cloud asset by its ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cloud asset retrieved successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CloudAsset.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cloud asset not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "status": 404,
                              "error": "Resource not found",
                              "message": "Asset not found: asset-001"
                            }
                            """)
            )
    )
    public CloudAsset findById(
            @Parameter(
                    description = "Unique cloud asset ID",
                    example = "asset-001"
            )
            @PathVariable String id) {

        return assetService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a cloud asset",
            description = "Updates an existing cloud asset using its ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cloud asset updated successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CloudAsset.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid fields, malformed JSON or mismatched IDs",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            oneOf = {
                                    ValidationErrorResponse.class,
                                    ApiErrorResponse.class
                            }
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cloud asset not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "status": 404,
                              "error": "Resource not found",
                              "message": "Asset not found: asset-001"
                            }
                            """)
            )
    )
    public CloudAsset update(
            @Parameter(
                    description = "Unique cloud asset ID",
                    example = "asset-001"
            )
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Updated cloud asset information",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CloudAsset.class),
                            examples = @ExampleObject(
                                    name = "Updated AWS Database",
                                    value = """
                                            {
                                              "id": "asset-001",
                                              "name": "Production Database Updated",
                                              "type": "DATABASE",
                                              "provider": "AWS",
                                              "region": "us-east-1"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody CloudAsset asset) {

        return assetService.update(id, asset);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a cloud asset",
            description = "Deletes a cloud asset by its ID."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Cloud asset deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cloud asset not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "status": 404,
                              "error": "Resource not found",
                              "message": "Asset not found: asset-001"
                            }
                            """)
            )
    )
    public ResponseEntity<Void> deleteById(
            @Parameter(
                    description = "Unique cloud asset ID",
                    example = "asset-001"
            )
            @PathVariable String id) {

        assetService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
