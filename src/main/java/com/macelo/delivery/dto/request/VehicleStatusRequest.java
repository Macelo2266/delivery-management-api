package com.macelo.delivery.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleStatusRequest {

    @NotNull(message = "O campo 'active' é obrigatório")
    private Boolean active;
}
