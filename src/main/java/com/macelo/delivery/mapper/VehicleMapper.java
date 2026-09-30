package com.macelo.delivery.mapper;

import com.macelo.delivery.dto.request.VehicleRequest;
import com.macelo.delivery.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {


    public Vehicle toEntity(VehicleRequest request) {
        return Vehicle.builder()
                .plate(request.getPlate())
                .model(request.getModel())
                .brand(request.getBrand())
                .year(request.getYear())
                .active(true)
                .build();
    }

    public void updateEntity(Vehicle vehicle, VehicleRequest request) {
        vehicle.setPlate(request.getPlate());
        vehicle.setModel(request.getModel());
        vehicle.setBrand(request.getBrand());
        vehicle.setYear(request.getYear());
    }
}
