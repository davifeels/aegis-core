package io.aegiscore.asset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CloudAsset(

        @NotBlank(message = "ID is required")
        @Size(max = 50, message = "ID must have at most 50 characters")
        String id,

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must have at most 100 characters")
        String name,

        @NotBlank(message = "Type is required")
        @Pattern(
                regexp = "DATABASE|VIRTUAL_MACHINE|STORAGE|NETWORK",
                message = "Type must be DATABASE, VIRTUAL_MACHINE, STORAGE or NETWORK"
        )
        String type,

        @NotBlank(message = "Provider is required")
        @Pattern(
                regexp = "AWS|AZURE|GCP",
                message = "Provider must be AWS, AZURE or GCP"
        )
        String provider,

        @NotBlank(message = "Region is required")
        @Size(max = 50, message = "Region must have at most 50 characters")
        String region
) {
}
