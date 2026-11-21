package br.ufc.loading.requests.application.port.in;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.model.PageQuery;
import br.ufc.loading.requests.application.model.PageResult;
import br.ufc.loading.requests.domain.model.AuditEntry;

public interface ListAuditEntriesUseCase {

    PageResult<AuditEntry> execute(PageQuery pageQuery, AuthenticatedActor actor);
}
