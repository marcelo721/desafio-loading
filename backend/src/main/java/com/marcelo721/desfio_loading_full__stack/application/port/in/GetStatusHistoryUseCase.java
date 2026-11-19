package com.marcelo721.desfio_loading_full__stack.application.port.in;



import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.domain.model.StatusHistoryEntry;

import java.util.List;
import java.util.UUID;

public interface GetStatusHistoryUseCase {

    List<StatusHistoryEntry> execute(UUID requestId, AuthenticatedActor actor);
}
