package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.model.PageQuery;
import br.ufc.loading.requests.application.model.PageResult;
import br.ufc.loading.requests.application.port.in.ListAuditEntriesUseCase;
import br.ufc.loading.requests.application.port.out.AuditLogPort;
import br.ufc.loading.requests.domain.exception.ForbiddenOperationException;
import br.ufc.loading.requests.domain.model.AuditEntry;
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
