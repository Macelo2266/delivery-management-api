package com.macelo.delivery.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
    private String name;

    @NotBlank(message = "O documento é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "Documento deve ser um CPF válido (11 dígitos, sem pontuação)")
    private String document;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos, sem pontuação")
    private String phone;

    @NotBlank(message = "A CNH é obrigatória")
    @Pattern(regexp = "\\d{11}", message = "CNH deve conter 11 dígitos")
    private String licenseNumber;
}
