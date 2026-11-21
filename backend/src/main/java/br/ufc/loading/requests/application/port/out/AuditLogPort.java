package br.ufc.loading.requests.application.port.out;

import br.ufc.loading.requests.application.model.PageQuery;
import br.ufc.loading.requests.application.model.PageResult;
import br.ufc.loading.requests.domain.model.AuditEntry;

public interface AuditLogPort {

    AuditEntry save(AuditEntry entry);

    PageResult<AuditEntry> findAll(PageQuery pageQuery);
}
