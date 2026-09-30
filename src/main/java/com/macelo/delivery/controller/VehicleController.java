package com.macelo.delivery.controller;

import com.macelo.delivery.dto.request.VehicleRequest;
import com.macelo.delivery.dto.request.VehicleStatusRequest;
import com.macelo.delivery.dto.response.VehicleResponse;
import com.macelo.delivery.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(vehicleService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VehicleResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody VehicleStatusRequest request) {
        return ResponseEntity.ok(vehicleService.updateStatus(id, request));
    }
}
