package io.aegiscore.audit.service;

import io.aegiscore.audit.dto.AuditLogPageResponse;
import io.aegiscore.audit.dto.AuditLogResponse;
import io.aegiscore.audit.entity.AssetAuditRecordEntity;
import io.aegiscore.audit.repository.AssetAuditRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetAuditQueryService {

    private final AssetAuditRepository auditRepository;

    public AssetAuditQueryService(AssetAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(readOnly = true)
    public AuditLogPageResponse search(
            String action,
            String assetId,
            Instant fromDate,
            Instant toDate,
            int page,
            int size
    ) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be zero or greater"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Size must be between 1 and 100"
            );
        }

        if (fromDate != null && toDate != null
                && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate must be before or equal to toDate"
            );
        }

        Specification<AssetAuditRecordEntity> specification =
                (root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (action != null && !action.isBlank()) {
                        predicates.add(
                                cb.equal(root.get("action"), action.trim())
                        );
                    }

                    if (assetId != null && !assetId.isBlank()) {
                        predicates.add(
                                cb.equal(root.get("assetId"), assetId.trim())
                        );
                    }

                    if (fromDate != null) {
                        predicates.add(
                                cb.greaterThanOrEqualTo(
                                        root.get("occurredAt"), fromDate
                                )
                        );
                    }

                    if (toDate != null) {
                        predicates.add(
                                cb.lessThanOrEqualTo(
                                        root.get("occurredAt"), toDate
                                )
                        );
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                };

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("occurredAt"),
                        Sort.Order.desc("id")
                )
        );

        Page<AuditLogResponse> results = auditRepository
                .findAll(specification, pageable)
                .map(AuditLogResponse::from);

        return new AuditLogPageResponse(
                results.getContent(),
                results.getNumber(),
                results.getSize(),
                results.getTotalElements(),
                results.getTotalPages()
        );
    }
}
