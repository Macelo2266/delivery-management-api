package com.macelo.delivery.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRequest {

    @NotBlank(message = "A placa é obrigatória")
    @Pattern(
            regexp = "[A-Z]{3}\\d[A-Z0-9]\\d{2}",
            message = "Placa inválida — use o formato Mercosul (ex: ABC1D23) ou padrão antigo (ex: ABC1234)"
    )
    private String plate;

    @NotBlank(message = "O modelo é obrigatório")
    @Size(min = 2, max = 80, message = "O modelo deve ter entre 2 e 80 caracteres")
    private String model;

    @NotBlank(message = "A marca é obrigatória")
    @Size(min = 2, max = 80, message = "A marca deve ter entre 2 e 80 caracteres")
    private String brand;

    @NotNull(message = "O ano é obrigatório")
    @Min(value = 1980, message = "Ano inválido")
    private Integer year;
}
