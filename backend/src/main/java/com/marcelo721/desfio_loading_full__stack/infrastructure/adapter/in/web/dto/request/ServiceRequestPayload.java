package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.in.web.dto.request;


import com.marcelo721.desfio_loading_full__stack.domain.enums.Priority;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ServiceRequestPayload(

        @Schema(example = "Impressora do setor financeiro não imprime")
        @NotBlank(message = "O título é obrigatório.")
        @Size(max = ServiceRequest.TITLE_MAX_LENGTH, message = "O título deve ter no máximo {max} caracteres.")
        String title,

        @Schema(example = "A impressora do 2º andar exibe erro de papel atolado desde ontem.")
        @NotBlank(message = "A descrição é obrigatória.")
        @Size(max = ServiceRequest.DESCRIPTION_MAX_LENGTH,
                message = "A descrição deve ter no máximo {max} caracteres.")
        String description,

        @Schema(example = "Infraestrutura")
        @NotBlank(message = "A categoria é obrigatória.")
        @Size(max = ServiceRequest.CATEGORY_MAX_LENGTH, message = "A categoria deve ter no máximo {max} caracteres.")
        String category,

        @Schema(description = "LOW, MEDIUM ou HIGH", example = "HIGH")
        @NotNull(message = "A prioridade é obrigatória (LOW, MEDIUM ou HIGH).")
        Priority priority) {
}
