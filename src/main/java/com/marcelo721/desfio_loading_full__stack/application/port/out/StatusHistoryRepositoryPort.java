package com.marcelo721.desfio_loading_full__stack.application.port.out;


import com.marcelo721.desfio_loading_full__stack.domain.model.StatusHistoryEntry;

import java.util.List;
import java.util.UUID;

public interface StatusHistoryRepositoryPort {

    StatusHistoryEntry save(StatusHistoryEntry entry);

    List<StatusHistoryEntry> findByRequestId(UUID requestId);
}
