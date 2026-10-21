
package io.aegiscore.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "AEGIS_ADMIN_USERNAME=test-admin",
        "AEGIS_ADMIN_PASSWORD=test-password"
})
class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ASSET_JSON = """
            {
                "id": "asset-001",
                "name": "Production Database",
                "type": "DATABASE",
                "provider": "AWS",
                "region": "sa-east-1"
            }
            """;

    @Test
    void shouldRejectUnauthenticatedAssetListRequest() throws Exception {
        mockMvc.perform(get("/api/v1/assets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedAssetDetailsRequest() throws Exception {
        mockMvc.perform(get("/api/v1/assets/asset-001"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedAssetCreationRequest() throws Exception {
        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ASSET_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedAssetUpdateRequest() throws Exception {
        mockMvc.perform(put("/api/v1/assets/asset-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ASSET_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedAssetDeletionRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/assets/asset-001"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectInvalidPassword() throws Exception {
        mockMvc.perform(get("/api/v1/assets")
                        .with(httpBasic("test-admin", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnknownUser() throws Exception {
        mockMvc.perform(get("/api/v1/assets")
                        .with(httpBasic("unknown-user", "test-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAuthenticatedApiRequest() throws Exception {
        mockMvc.perform(get("/api/v1/assets")
                        .with(httpBasic("test-admin", "test-password")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowPublicSwaggerDocumentation() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldStoreAdminPasswordAsEncodedValue() {
        UserDetails admin =
                userDetailsService.loadUserByUsername("test-admin");

        assertFalse(admin.getPassword().equals("test-password"));
        assertTrue(
                passwordEncoder.matches(
                        "test-password",
                        admin.getPassword()
                )
        );
    }
}
