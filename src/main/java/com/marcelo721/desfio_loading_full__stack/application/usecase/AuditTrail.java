package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.port.out.AuditLogPort;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditOperation;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditedEntity;
import com.marcelo721.desfio_loading_full__stack.domain.model.AuditEntry;

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
