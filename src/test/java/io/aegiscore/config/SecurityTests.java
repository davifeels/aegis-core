
package io.aegiscore.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "AEGIS_ADMIN_USERNAME=test-admin",
        "AEGIS_ADMIN_PASSWORD=test-password",
        "AEGIS_USER_USERNAME=test-user",
        "AEGIS_USER_PASSWORD=test-user-password"
})
class SecurityTests {

    private static final String ASSETS_URL = "/api/v1/assets";
    private static final String ADMIN_USERNAME = "test-admin";
    private static final String ADMIN_PASSWORD = "test-password";
    private static final String USER_USERNAME = "test-user";
    private static final String USER_PASSWORD = "test-user-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldEncodeAdminPassword() {
        UserDetails admin =
                userDetailsService.loadUserByUsername(ADMIN_USERNAME);

        assertTrue(passwordEncoder.matches(
                ADMIN_PASSWORD,
                admin.getPassword()
        ));
        assertFalse(admin.getPassword().equals(ADMIN_PASSWORD));
    }

    @Test
    void shouldEncodeRegularUserPassword() {
        UserDetails user =
                userDetailsService.loadUserByUsername(USER_USERNAME);

        assertTrue(passwordEncoder.matches(
                USER_PASSWORD,
                user.getPassword()
        ));
        assertFalse(user.getPassword().equals(USER_PASSWORD));
    }

    @Test
    void shouldAllowUnauthenticatedSwaggerAccess() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectUnauthenticatedAssetReadRequest() throws Exception {
        mockMvc.perform(get(ASSETS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedAssetCreationRequest() throws Exception {
        mockMvc.perform(post(ASSETS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedAssetUpdateRequest() throws Exception {
        mockMvc.perform(put(ASSETS_URL + "/security-update-unauth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedAssetDeletionRequest() throws Exception {
        mockMvc.perform(delete(ASSETS_URL + "/security-delete-unauth"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowRegularUserToReadAssets() throws Exception {
        mockMvc.perform(get(ASSETS_URL)
                        .with(httpBasic(USER_USERNAME, USER_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowRegularUserToReadAssetById() throws Exception {
        mockMvc.perform(get(ASSETS_URL + "/security-user-read-missing")
                        .with(httpBasic(USER_USERNAME, USER_PASSWORD)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldForbidRegularUserFromCreatingAssets() throws Exception {
        mockMvc.perform(post(ASSETS_URL)
                        .with(httpBasic(USER_USERNAME, USER_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetJson(
                                "security-user-create-forbidden")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldForbidRegularUserFromUpdatingAssets() throws Exception {
        mockMvc.perform(put(ASSETS_URL + "/security-user-update-forbidden")
                        .with(httpBasic(USER_USERNAME, USER_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetJson(
                                "security-user-update-forbidden")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldForbidRegularUserFromDeletingAssets() throws Exception {
        mockMvc.perform(delete(
                        ASSETS_URL + "/security-user-delete-forbidden")
                        .with(httpBasic(USER_USERNAME, USER_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToReadAssets() throws Exception {
        mockMvc.perform(get(ASSETS_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAdminToReadAssetById() throws Exception {
        String id = "security-admin-read-001";

        createAssetAsAdmin(id);

        mockMvc.perform(get(ASSETS_URL + "/" + id)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Security Test Database"));
    }

    @Test
    void shouldAllowAdminToCreateAsset() throws Exception {
        String id = "security-admin-create-001";

        mockMvc.perform(post(ASSETS_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetJson(id)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Security Test Database"));
    }

    @Test
    void shouldLogAuthenticatedAdminForAssetCreation(
            CapturedOutput output) throws Exception {

        String id = "security-audit-admin-001";

        mockMvc.perform(post(ASSETS_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetJson(id)))
                .andExpect(status().isCreated());

        assertTrue(output.getAll()
                .contains("AUDIT action=CREATE actor=test-admin assetId=" + id));
    }

    @Test
    void shouldAllowAdminToUpdateAsset() throws Exception {
        String id = "security-admin-update-001";

        createAssetAsAdmin(id);

        String updatedAsset = """
                {
                    "id": "security-admin-update-001",
                    "name": "Security Test Database Updated",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "us-east-1"
                }
                """;

        mockMvc.perform(put(ASSETS_URL + "/" + id)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAsset))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name")
                        .value("Security Test Database Updated"))
                .andExpect(jsonPath("$.region").value("us-east-1"));
    }

    @Test
    void shouldAllowAdminToDeleteAsset() throws Exception {
        String id = "security-admin-delete-001";

        createAssetAsAdmin(id);

        mockMvc.perform(delete(ASSETS_URL + "/" + id)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(ASSETS_URL + "/" + id)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isNotFound());
    }

    private void createAssetAsAdmin(String id) throws Exception {
        mockMvc.perform(post(ASSETS_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validAssetJson(id)))
                .andExpect(status().isCreated());
    }

    private String validAssetJson(String id) {
        return """
                {
                    "id": "%s",
                    "name": "Security Test Database",
                    "type": "DATABASE",
                    "provider": "AWS",
                    "region": "sa-east-1"
                }
                """.formatted(id);
    }
}
