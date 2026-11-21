package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.port.out.AuditLogPort;
import br.ufc.loading.requests.domain.enums.AuditOperation;
import br.ufc.loading.requests.domain.enums.AuditedEntity;
import br.ufc.loading.requests.domain.model.AuditEntry;

import java.time.Clock;
import java.util.UUID;

public class AuditTrail {

    private final AuditLogPort auditLogPort;
    private final Clock clock;

    public AuditTrail(AuditLogPort auditLogPort, Clock clock) {
        this.auditLogPort = auditLogPort;
        this.clock = clock;
    }

    public void record(AuditedEntity entityType, UUID entityId, AuditOperation operation,
                       AuthenticatedActor actor, String details) {
        auditLogPort.save(AuditEntry.create(entityType, entityId, operation,
                actor.id(), actor.name(), clock.instant(), details));
    }
}
