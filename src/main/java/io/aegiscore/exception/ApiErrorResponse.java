
package io.aegiscore.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API error response")
public record ApiErrorResponse(

        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "Short description of the error", example = "Resource not found")
        String error,

        @Schema(description = "Detailed explanation of the error", example = "Asset not found: asset-001")
        String message
) {
}
