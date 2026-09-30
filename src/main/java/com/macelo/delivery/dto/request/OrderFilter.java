package com.macelo.delivery.dto.request;

import com.macelo.delivery.enums.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderFilter {

    private DeliveryStatus status;
    private String trackingCode;
    private Long customerId;
    private Long driverId;
    private LocalDate createdAt;
}
