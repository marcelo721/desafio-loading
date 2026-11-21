package br.ufc.loading.requests.infrastructure.adapter.out.persistence.entity;

import br.ufc.loading.requests.domain.enums.RequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "status_history")
public class StatusHistoryJpaEntity {

    @Id
    private UUID id;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "from_status")
    private RequestStatus fromStatus;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "to_status", nullable = false)
    private RequestStatus toStatus;

    @Column(name = "changed_by", nullable = false)
    private UUID changedBy;

    @Column(name = "changed_by_name", nullable = false, length = 100)
    private String changedByName;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    protected StatusHistoryJpaEntity() {
    }

    public StatusHistoryJpaEntity(UUID id, UUID requestId, RequestStatus fromStatus, RequestStatus toStatus,
                                  UUID changedBy, String changedByName, Instant changedAt) {
        this.id = id;
        this.requestId = requestId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.changedByName = changedByName;
        this.changedAt = changedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public RequestStatus getFromStatus() {
        return fromStatus;
    }

    public RequestStatus getToStatus() {
        return toStatus;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public Instant getChangedAt() {
        return changedAt;
    }
}
