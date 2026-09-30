package com.macelo.delivery.service;


import com.macelo.delivery.dto.request.DriverRequest;
import com.macelo.delivery.dto.request.DriverStatusRequest;
import com.macelo.delivery.dto.response.DriverResponse;
import com.macelo.delivery.entity.Driver;
import com.macelo.delivery.exception.BusinessException;
import com.macelo.delivery.exception.ResourceNotFoundException;
import com.macelo.delivery.mapper.DriverMapper;
import com.macelo.delivery.repository.DriverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Transactional
    public DriverResponse create(DriverRequest request) {
        if (driverRepository.existsByDocument(request.getDocument())) {
            throw new BusinessException("Documento já cadastrado: " + request.getDocument());
        }
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new BusinessException("CNH já cadastrada: " + request.getLicenseNumber());
        }
        Driver driver = driverMapper.toEntity(request);
        return DriverResponse.from(driverRepository.save(driver));
    }

    public Page<DriverResponse> findAll(Pageable pageable) {
        return driverRepository.findAll(pageable).map(DriverResponse::from);
    }

    public DriverResponse findById(Long id) {
        return DriverResponse.from(getDriverOrThrow(id));
    }

    @Transactional
    public DriverResponse update(Long id, DriverRequest request) {
        Driver driver = getDriverOrThrow(id);

        boolean documentChanged = !driver.getDocument().equals(request.getDocument());
        if (documentChanged && driverRepository.existsByDocument(request.getDocument())) {
            throw new BusinessException("Documento já cadastrado: " + request.getDocument());
        }

        boolean licenseChanged = !driver.getLicenseNumber().equals(request.getLicenseNumber());
        if (licenseChanged && driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new BusinessException("CNH já cadastrada: " + request.getLicenseNumber());
        }

        driverMapper.updateEntity(driver, request);
        return DriverResponse.from(driver);
    }

    @Transactional
    public DriverResponse updateStatus(Long id, DriverStatusRequest request) {
        Driver driver = getDriverOrThrow(id);
        driver.setActive(request.getActive());
        return DriverResponse.from(driver);
    }

    private Driver getDriverOrThrow(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado: id " + id));
    }
}
