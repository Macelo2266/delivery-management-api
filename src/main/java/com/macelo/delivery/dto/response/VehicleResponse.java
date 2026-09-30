package com.macelo.delivery.dto.response;

import com.macelo.delivery.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class VehicleResponse {

    private Long id;
    private String plate;
    private String model;
    private String brand;
    private Integer year;
    private boolean active;
    private LocalDateTime createdAt;

    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlate(),
                vehicle.getModel(),
                vehicle.getBrand(),
                vehicle.getYear(),
                vehicle.isActive(),
                vehicle.getCreatedAt()
        );
    }
}
