package com.marcelo721.desfio_loading_full__stack.application.port.out;


import com.marcelo721.desfio_loading_full__stack.application.model.PageQuery;
import com.marcelo721.desfio_loading_full__stack.application.model.PageResult;
import com.marcelo721.desfio_loading_full__stack.domain.model.AuditEntry;

public interface AuditLogPort {

    AuditEntry save(AuditEntry entry);

    PageResult<AuditEntry> findAll(PageQuery pageQuery);
}
