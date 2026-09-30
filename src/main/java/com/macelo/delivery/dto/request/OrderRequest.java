package com.macelo.delivery.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderRequest {

    @NotNull(message = "O cliente é obrigatório")
    private Long customerId;

    @NotNull(message = "O endereço de retirada é obrigatório")
    @Valid
    private AddressRequest pickupAddress;

    @NotNull(message = "O endereço de entrega é obrigatório")
    @Valid
    private AddressRequest deliveryAddress;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String description;

    @NotNull(message = "O peso é obrigatório")
    @Positive(message = "O peso deve ser maior que zero")
    private BigDecimal weight;

    @Future(message = "A previsão de entrega deve ser uma data futura")
    private LocalDateTime estimatedDeliveryAt;
}
