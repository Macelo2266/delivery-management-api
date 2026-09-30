package com.macelo.delivery.dto.response;

import com.macelo.delivery.entity.Driver;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DriverResponse {

    private Long id;
    private String name;
    private String document;
    private String phone;
    private String licenseNumber;
    private boolean active;
    private LocalDateTime createdAt;

    public static DriverResponse from(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getDocument(),
                driver.getPhone(),
                driver.getLicenseNumber(),
                driver.isActive(),
                driver.getCreatedAt()
        );
    }
}
