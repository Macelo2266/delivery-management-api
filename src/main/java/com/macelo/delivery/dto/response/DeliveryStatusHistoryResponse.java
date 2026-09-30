package com.macelo.delivery.dto.response;


import com.macelo.delivery.entity.DeliveryStatusHistory;
import com.macelo.delivery.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DeliveryStatusHistoryResponse {

    private Long id;
    private DeliveryStatus status;
    private String description;
    private LocalDateTime changedAt;
    private String changedBy;

    public static DeliveryStatusHistoryResponse from(DeliveryStatusHistory history) {
        return new DeliveryStatusHistoryResponse(
                history.getId(),
                history.getStatus(),
                history.getDescription(),
                history.getChangedAt(),
                history.getChangedBy()
        );
    }
}
