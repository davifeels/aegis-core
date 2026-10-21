package io.aegiscore.audit.controller;

import io.aegiscore.audit.dto.AuditLogPageResponse;
import io.aegiscore.audit.service.AssetAuditQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AssetAuditController {

    private final AssetAuditQueryService queryService;

    public AssetAuditController(
            AssetAuditQueryService queryService
    ) {
        this.queryService = queryService;
    }

    @GetMapping
    public AuditLogPageResponse search(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String assetId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return queryService.search(
                action,
                assetId,
                fromDate,
                toDate,
                page,
                size
        );
    }
}
