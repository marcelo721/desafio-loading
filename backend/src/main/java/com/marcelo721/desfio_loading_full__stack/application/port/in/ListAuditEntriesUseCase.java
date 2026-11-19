package com.marcelo721.desfio_loading_full__stack.application.port.in;


import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.model.PageQuery;
import com.marcelo721.desfio_loading_full__stack.application.model.PageResult;
import com.marcelo721.desfio_loading_full__stack.domain.model.AuditEntry;

public interface ListAuditEntriesUseCase {

    PageResult<AuditEntry> execute(PageQuery pageQuery, AuthenticatedActor actor);
}
