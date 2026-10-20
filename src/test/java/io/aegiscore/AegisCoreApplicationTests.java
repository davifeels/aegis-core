package io.aegiscore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AegisCoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldReturnPlatformInformation() throws Exception {
        mockMvc.perform(get("/api/v1/platform"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("AegisCore"))
                .andExpect(jsonPath("$.version").value("0.1.0"))
                .andExpect(jsonPath("$.status").value("operational"));
    }

    @Test
    void shouldRegisterValidCloudAsset() throws Exception {
        String assetJson = """
                {
                    "id": "asset-test-001",
                    "name": "database-test",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("asset-test-001"))
                .andExpect(jsonPath("$.name").value("database-test"))
                .andExpect(jsonPath("$.provider").value("AWS"));
    }

    @Test
    void shouldRejectInvalidCloudAsset() throws Exception {
        String assetJson = """
                {
                    "id": "asset-test-002",
                    "name": "",
                    "type": "BANCO",
                    "provider": "OUTRA_NUVEM",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void shouldRejectBlankAssetId() throws Exception {
        String assetJson = """
                {
                    "id": "   ",
                    "name": "database-test",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void shouldRejectAssetIdLongerThan50Characters() throws Exception {
        String longId = "a".repeat(51);

        String assetJson = """
                {
                    "id": "%s",
                    "name": "database-test",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """.formatted(longId);

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void shouldRejectAssetNameLongerThan100Characters() throws Exception {
        String longName = "a".repeat(101);

        String assetJson = """
                {
                    "id": "asset-long-name-test",
                    "name": "%s",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """.formatted(longName);

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void shouldRejectInvalidAssetType() throws Exception {
        String assetJson = """
                {
                    "id": "asset-invalid-type-test",
                    "name": "database-test",
                    "type": "BANCO",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void shouldReturn409WhenRegisteringDuplicateCloudAsset()
            throws Exception {
        String assetJson = """
                {
                    "id": "asset-duplicate-test-001",
                    "name": "database-duplicate-test",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Resource conflict"));
    }

    @Test
    void shouldFindCloudAssetById() throws Exception {
        String assetJson = """
                {
                    "id": "asset-find-test-001",
                    "name": "database-find-test",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/assets/asset-find-test-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("asset-find-test-001"))
                .andExpect(jsonPath("$.name").value("database-find-test"));
    }

    @Test
    void shouldListCloudAssets() throws Exception {
        String assetJson = """
                {
                    "id": "asset-list-test-001",
                    "name": "database-list-test",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == 'asset-list-test-001')]")
                        .isNotEmpty());
    }

    @Test
    void shouldReturn404WhenCloudAssetDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/assets/id-que-nao-existe-test"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message")
                        .value("Asset not found: id-que-nao-existe-test"));
    }

    @Test
    void shouldDeleteExistingCloudAsset() throws Exception {
        String assetJson = """
                {
                    "id": "asset-delete-test-001",
                    "name": "database-delete-test",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/assets/asset-delete-test-001"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/assets/asset-delete-test-001"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonexistentCloudAsset()
            throws Exception {
        mockMvc.perform(delete(
                        "/api/v1/assets/asset-delete-missing-test"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Resource not found"));
    }

    @Test
    void shouldUpdateExistingCloudAsset() throws Exception {
        String originalAsset = """
                {
                    "id": "asset-update-test-001",
                    "name": "database-original",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(originalAsset))
                .andExpect(status().isCreated());

        String updatedAsset = """
                {
                    "id": "asset-update-test-001",
                    "name": "database-updated",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "us-east-1"
                }
                """;

        mockMvc.perform(put("/api/v1/assets/asset-update-test-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAsset))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("database-updated"))
                .andExpect(jsonPath("$.region").value("us-east-1"));

        mockMvc.perform(get("/api/v1/assets/asset-update-test-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("database-updated"))
                .andExpect(jsonPath("$.region").value("us-east-1"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonexistentCloudAsset()
            throws Exception {
        String updatedAsset = """
                {
                    "id": "asset-update-missing-test",
                    "name": "database-updated",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(put(
                        "/api/v1/assets/asset-update-missing-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAsset))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Resource not found"));
    }

    @Test
    void shouldReturn400WhenUpdatingWithDifferentIds() throws Exception {
        String assetJson = """
                {
                    "id": "asset-put-url-001",
                    "name": "database-original",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isCreated());

        String updatedAsset = """
                {
                    "id": "asset-put-body-002",
                    "name": "database-updated",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "us-east-1"
                }
                """;

        mockMvc.perform(put("/api/v1/assets/asset-put-url-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAsset))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid request"))
                .andExpect(jsonPath("$.message")
                        .value("Asset ID in the URL must match the asset ID in the body"));
    }

    @Test
    void shouldRejectInvalidCloudProvider() throws Exception {
        String assetJson = """
                {
                    "id": "asset-invalid-provider-test",
                    "name": "database-test",
                    "type": "DATABASE",
                    "provider": "OUTRA_NUVEM",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Validation failed"));
    }

    @Test
    void shouldRejectBlankCloudAssetRegion() throws Exception {
        String assetJson = """
                {
                    "id": "asset-blank-region-test",
                    "name": "database-test",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": " "
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Validation failed"));
    }

    @Test
    void shouldRejectInvalidCloudAssetUpdate() throws Exception {
        String originalAsset = """
                {
                    "id": "asset-invalid-update-test",
                    "name": "database-original",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(originalAsset))
                .andExpect(status().isCreated());

        String invalidAsset = """
                {
                    "id": "asset-invalid-update-test",
                    "name": "",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(put(
                        "/api/v1/assets/asset-invalid-update-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAsset))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Validation failed"));
    }

    @Test
    void shouldReturnNoContentWhenDeletingCloudAsset()
            throws Exception {
        String assetJson = """
                {
                    "id": "asset-delete-no-content-test",
                    "name": "database-test",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isCreated());

        mockMvc.perform(delete(
                        "/api/v1/assets/asset-delete-no-content-test"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void shouldReturn400ForMalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Malformed request"));
    }

    @Test
    void shouldIdentifyInvalidFieldInValidationErrors() throws Exception {
        String assetJson = """
                {
                    "id": "asset-field-error-test",
                    "name": "",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """;

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assetJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0].field").value("name"))
                .andExpect(jsonPath("$.details[0].message")
                        .value("Name is required"));
    }

}
