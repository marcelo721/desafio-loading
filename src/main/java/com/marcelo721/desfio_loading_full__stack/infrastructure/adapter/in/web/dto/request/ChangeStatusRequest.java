package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.dto.request;

import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(

        @Schema(description = "Próximo status: OPEN, IN_PROGRESS ou COMPLETED", example = "IN_PROGRESS")
        @NotNull(message = "O novo status é obrigatório (OPEN, IN_PROGRESS ou COMPLETED).")
        RequestStatus status) {
}
