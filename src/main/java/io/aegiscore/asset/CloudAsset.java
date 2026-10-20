
package io.aegiscore.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        name = "CloudAsset",
        description = "A cloud asset managed by AegisCore"
)
public record CloudAsset(

        @Schema(
                description = "Unique identifier of the cloud asset",
                example = "asset-001",
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "ID is required")
        @Size(max = 50, message = "ID must have at most 50 characters")
        String id,

        @Schema(
                description = "Display name of the cloud asset",
                example = "Production Database",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must have at most 100 characters")
        String name,

        @Schema(
                description = "Type of cloud asset",
                example = "DATABASE",
                allowableValues = {
                        "DATABASE",
                        "VIRTUAL_MACHINE",
                        "STORAGE",
                        "NETWORK"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Type is required")
        @Pattern(
                regexp = "DATABASE|VIRTUAL_MACHINE|STORAGE|NETWORK",
                message = "Type must be DATABASE, VIRTUAL_MACHINE, STORAGE or NETWORK"
        )
        String type,

        @Schema(
                description = "Cloud service provider",
                example = "AWS",
                allowableValues = {"AWS", "AZURE", "GCP"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Provider is required")
        @Pattern(
                regexp = "AWS|AZURE|GCP",
                message = "Provider must be AWS, AZURE or GCP"
        )
        String provider,

        @Schema(
                description = "Cloud region where the asset is located",
                example = "us-east-1",
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Region is required")
        @Size(max = 50, message = "Region must have at most 50 characters")
        String region
) {
}
