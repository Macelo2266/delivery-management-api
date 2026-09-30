package com.macelo.delivery.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverAssignmentRequest {

    @NotNull(message = "O motorista é obrigatório")
    private Long driverId;
}
