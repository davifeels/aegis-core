
package io.aegiscore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform")
@Tag(
        name = "Platform",
        description = "Platform information and operational status"
)
public class PlatformController {

    @GetMapping
    @Operation(
            summary = "Get platform information",
            description = """
                    Returns the AegisCore platform name, version,
                    description and current operational status.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Platform information retrieved successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            type = "object",
                            example = """
                                    {
                                      "name": "AegisCore",
                                      "version": "0.1.0",
                                      "description": "Cloud Security and Operations Platform",
                                      "status": "operational"
                                    }
                                    """
                    ),
                    examples = @ExampleObject(
                            name = "Platform Information",
                            summary = "Example platform status",
                            value = """
                                    {
                                      "name": "AegisCore",
                                      "version": "0.1.0",
                                      "description": "Cloud Security and Operations Platform",
                                      "status": "operational"
                                    }
                                    """
                    )
            )
    )
    public Map<String, String> getPlatformInfo() {
        return Map.of(
                "name", "AegisCore",
                "version", "0.1.0",
                "description", "Cloud Security and Operations Platform",
                "status", "operational"
        );
    }
}
