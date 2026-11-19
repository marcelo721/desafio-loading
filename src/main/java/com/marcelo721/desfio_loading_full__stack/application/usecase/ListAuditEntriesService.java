package com.marcelo721.desfio_loading_full__stack.application.usecase;


import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.model.PageQuery;
import com.marcelo721.desfio_loading_full__stack.application.model.PageResult;
import com.marcelo721.desfio_loading_full__stack.application.port.in.ListAuditEntriesUseCase;
import com.marcelo721.desfio_loading_full__stack.application.port.out.AuditLogPort;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.ForbiddenOperationException;
import com.marcelo721.desfio_loading_full__stack.domain.model.AuditEntry;
import org.springframework.transaction.annotation.Transactional;

public class ListAuditEntriesService implements ListAuditEntriesUseCase {

    private final AuditLogPort auditLogPort;

    public ListAuditEntriesService(AuditLogPort auditLogPort) {
        this.auditLogPort = auditLogPort;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AuditEntry> execute(PageQuery pageQuery, AuthenticatedActor actor) {
        if (!actor.isAdmin()) {
            throw new ForbiddenOperationException("Apenas administradores podem consultar a auditoria.");
        }
        return auditLogPort.findAll(pageQuery);
    }
}
