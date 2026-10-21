package io.aegiscore.audit;

import io.aegiscore.audit.entity.AssetAuditRecordEntity;
import io.aegiscore.audit.repository.AssetAuditRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "AEGIS_ADMIN_USERNAME=test-admin",
        "AEGIS_ADMIN_PASSWORD=test-password",
        "AEGIS_USER_USERNAME=test-user",
        "AEGIS_USER_PASSWORD=test-user-password"
})
class AssetAuditControllerTests {

    private static final String AUDIT_URL = "/api/v1/audit-logs";
    private static final String ADMIN_USERNAME = "test-admin";
    private static final String ADMIN_PASSWORD = "test-password";
    private static final String USER_USERNAME = "test-user";
    private static final String USER_PASSWORD = "test-user-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssetAuditRepository auditRepository;

    @Test
    void shouldRejectUnauthenticatedAuditQuery() throws Exception {
        mockMvc.perform(get(AUDIT_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldForbidRegularUserFromReadingAuditLogs() throws Exception {
        mockMvc.perform(get(AUDIT_URL)
                        .with(httpBasic(USER_USERNAME, USER_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToReadAuditLogs() throws Exception {
        mockMvc.perform(get(AUDIT_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber());
    }

    @Test
    void shouldFilterAuditLogsByActionAndAssetId() throws Exception {
        String assetId = "audit-query-filter-001";

        auditRepository.saveAndFlush(
                new AssetAuditRecordEntity("CREATE", "test-admin", assetId)
        );

        mockMvc.perform(get(AUDIT_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .param("action", "CREATE")
                        .param("assetId", assetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].action").value("CREATE"))
                .andExpect(jsonPath("$.content[0].actor").value("test-admin"))
                .andExpect(jsonPath("$.content[0].assetId").value(assetId))
                .andExpect(jsonPath("$.content[0].id").isNumber())
                .andExpect(jsonPath("$.content[0].occurredAt").isNotEmpty());
    }

    @Test
    void shouldApplyPaginationParameters() throws Exception {
        mockMvc.perform(get(AUDIT_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    void shouldRejectPageSizeGreaterThanMaximum() throws Exception {
        mockMvc.perform(get(AUDIT_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectZeroPageSize() throws Exception {
        mockMvc.perform(get(AUDIT_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNegativePageNumber() throws Exception {
        mockMvc.perform(get(AUDIT_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvertedDateRange() throws Exception {
        mockMvc.perform(get(AUDIT_URL)
                        .with(httpBasic(ADMIN_USERNAME, ADMIN_PASSWORD))
                        .param("fromDate", "2026-10-21T12:00:00Z")
                        .param("toDate", "2026-10-20T12:00:00Z"))
                .andExpect(status().isBadRequest());
    }
}
