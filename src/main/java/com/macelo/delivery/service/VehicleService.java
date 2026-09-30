package com.macelo.delivery.service;

import com.macelo.delivery.dto.request.VehicleRequest;
import com.macelo.delivery.dto.request.VehicleStatusRequest;
import com.macelo.delivery.dto.response.VehicleResponse;
import com.macelo.delivery.entity.Vehicle;
import com.macelo.delivery.exception.BusinessException;
import com.macelo.delivery.exception.ResourceNotFoundException;
import com.macelo.delivery.mapper.VehicleMapper;
import com.macelo.delivery.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByPlate(request.getPlate())) {
            throw new BusinessException("Placa já cadastrada: " + request.getPlate());
        }
        Vehicle vehicle = vehicleMapper.toEntity(request);
        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    public Page<VehicleResponse> findAll(Pageable pageable) {
        return vehicleRepository.findAll(pageable).map(VehicleResponse::from);
    }

    public VehicleResponse findById(Long id) {
        return VehicleResponse.from(getVehicleOrThrow(id));
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = getVehicleOrThrow(id);

        boolean plateChanged = !vehicle.getPlate().equals(request.getPlate());
        if (plateChanged && vehicleRepository.existsByPlate(request.getPlate())) {
            throw new BusinessException("Placa já cadastrada: " + request.getPlate());
        }

        vehicleMapper.updateEntity(vehicle, request);
        return VehicleResponse.from(vehicle);
    }

    @Transactional
    public VehicleResponse updateStatus(Long id, VehicleStatusRequest request) {
        Vehicle vehicle = getVehicleOrThrow(id);
        vehicle.setActive(request.getActive());
        return VehicleResponse.from(vehicle);
    }

    private Vehicle getVehicleOrThrow(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado: id " + id));
    }
}
