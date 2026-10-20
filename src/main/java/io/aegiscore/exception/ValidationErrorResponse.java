
package io.aegiscore.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "API request validation error")
public record ValidationErrorResponse(

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Short description of the error", example = "Validation failed")
        String error,

        @Schema(description = "List of invalid fields")
        List<ValidationDetail> details
) {

    @Schema(description = "Details of a field validation error")
    public record ValidationDetail(

            @Schema(description = "Name of the invalid field", example = "name")
            String field,

            @Schema(description = "Validation failure reason", example = "Name is required")
            String message
    ) {
    }
}
