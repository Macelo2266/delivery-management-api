package com.macelo.delivery.repository;

import com.macelo.delivery.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository  extends JpaRepository<Driver, Long> {

    boolean existsByDocument(String document);

    boolean existsByLicenseNumber(String licenseNumber);
}
