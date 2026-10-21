
package io.aegiscore.audit;

import io.aegiscore.audit.service.AssetAuditPersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class AssetAuditListenerTests {

    @Mock
    private AssetAuditPersistenceService persistenceService;

    private AssetAuditListener listener;

    @BeforeEach
    void setUp() {
        listener = new AssetAuditListener(persistenceService);
    }

    @Test
    void shouldPersistAuditEventWithExpectedValues() {
        AssetAuditEvent event = new AssetAuditEvent(
                "CREATE",
                "test-user",
                "asset-listener-001"
        );

        listener.handle(event);

        verify(persistenceService).record(
                "CREATE",
                "test-user",
                "asset-listener-001"
        );
        verifyNoMoreInteractions(persistenceService);
    }

    @Test
    void shouldReplaceBlankFieldsWithUnknown() {
        AssetAuditEvent event = new AssetAuditEvent(
                " ",
                null,
                ""
        );

        listener.handle(event);

        verify(persistenceService).record(
                "unknown",
                "unknown",
                "unknown"
        );
        verifyNoMoreInteractions(persistenceService);
    }

    @Test
    void shouldReplaceControlCharactersInFields() {
        AssetAuditEvent event = new AssetAuditEvent(
                "CRE\nATE",
                "test\tuser",
                "asset\r001"
        );

        listener.handle(event);

        verify(persistenceService).record(
                "CRE_ATE",
                "test_user",
                "asset_001"
        );
        verifyNoMoreInteractions(persistenceService);
    }

    @Test
    void shouldNotPropagatePersistenceFailure() {
        AssetAuditEvent event = new AssetAuditEvent(
                "CREATE",
                "test-user",
                "asset-listener-002"
        );

        doThrow(new IllegalStateException("Simulated persistence failure"))
                .when(persistenceService)
                .record("CREATE", "test-user", "asset-listener-002");

        assertDoesNotThrow(() -> listener.handle(event));

        verify(persistenceService).record(
                "CREATE",
                "test-user",
                "asset-listener-002"
        );
        verifyNoMoreInteractions(persistenceService);
    }
}
