
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
        "AEGIS_ADMIN_PASSWORD=test-password",
        "AEGIS_USER_USERNAME=test-user",
        "AEGIS_USER_PASSWORD=test-user-password"
})
class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldEncodeAdminPassword() {
        UserDetails admin = userDetailsService.loadUserByUsername("test-admin");

        assertTrue(passwordEncoder.matches(
                "test-password",
                admin.getPassword()
        ));
        assertFalse(admin.getPassword().equals("test-password"));
    }

    @Test
    void shouldEncodeRegularUserPassword() {
        UserDetails user = userDetailsService.loadUserByUsername("test-user");

        assertTrue(passwordEncoder.matches(
                "test-user-password",
                user.getPassword()
        ));
        assertFalse(user.getPassword().equals("test-user-password"));
    }

    @Test
    void shouldAllowUnauthenticatedSwaggerAccess() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectUnauthenticatedAssetReadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/assets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedAssetCreationRequest() throws Exception {
        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedAssetUpdateRequest() throws Exception {
        mockMvc.perform(put("/api/v1/assets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthenticatedAssetDeletionRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/assets/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowRegularUserToReadAssets() throws Exception {
        mockMvc.perform(get("/api/v1/assets")
                        .with(httpBasic("test-user", "test-user-password")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldForbidRegularUserFromCreatingAssets() throws Exception {
        mockMvc.perform(post("/api/v1/assets")
                        .with(httpBasic("test-user", "test-user-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldForbidRegularUserFromUpdatingAssets() throws Exception {
        mockMvc.perform(put("/api/v1/assets/1")
                        .with(httpBasic("test-user", "test-user-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldForbidRegularUserFromDeletingAssets() throws Exception {
        mockMvc.perform(delete("/api/v1/assets/1")
                        .with(httpBasic("test-user", "test-user-password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToReadAssets() throws Exception {
        mockMvc.perform(get("/api/v1/assets")
                        .with(httpBasic("test-admin", "test-password")))
                .andExpect(status().isOk());
    }
}
