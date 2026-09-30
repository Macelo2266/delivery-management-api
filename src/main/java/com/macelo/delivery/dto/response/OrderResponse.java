package com.macelo.delivery.dto.response;


import com.macelo.delivery.entity.Order;
import com.macelo.delivery.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String trackingCode;
    private CustomerResponse customer;
    private AddressResponse pickupAddress;
    private AddressResponse deliveryAddress;
    private String description;
    private BigDecimal weight;
    private DeliveryStatus status;
    private DriverResponse driver;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime estimatedDeliveryAt;
    private LocalDateTime deliveredAt;

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTrackingCode(),
                CustomerResponse.from(order.getCustomer()),
                AddressResponse.from(order.getPickupAddress()),
                AddressResponse.from(order.getDeliveryAddress()),
                order.getDescription(),
                order.getWeight(),
                order.getStatus(),
                order.getDriver() != null ? DriverResponse.from(order.getDriver()) : null,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getEstimatedDeliveryAt(),
                order.getDeliveredAt()
        );
    }
}
