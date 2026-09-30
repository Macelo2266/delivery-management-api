package com.macelo.delivery.mapper;

import com.macelo.delivery.dto.request.DriverRequest;
import com.macelo.delivery.entity.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public Driver toEntity(DriverRequest request) {
        return Driver.builder()
                .name(request.getName())
                .document(request.getDocument())
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .active(true)
                .build();
    }

    public void updateEntity(Driver driver, DriverRequest request) {
        driver.setName(request.getName());
        driver.setDocument(request.getDocument());
        driver.setPhone(request.getPhone());
        driver.setLicenseNumber(request.getLicenseNumber());
    }
}
