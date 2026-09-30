package com.macelo.delivery.dto.request;

import com.macelo.delivery.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {

    @NotNull(message = "O novo status é obrigatório")
    private DeliveryStatus status;

    @Size(max = 300, message = "A descrição deve ter no máximo 300 caracteres")
    private String description;
}
