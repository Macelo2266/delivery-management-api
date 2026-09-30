package com.macelo.delivery.controller;


import com.macelo.delivery.dto.request.DriverRequest;
import com.macelo.delivery.dto.request.DriverStatusRequest;
import com.macelo.delivery.dto.response.DriverResponse;
import com.macelo.delivery.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<Page<DriverResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(driverService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR') or @driverSecurity.isSelf(#id, authentication)")
    public ResponseEntity<DriverResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverResponse> update(@PathVariable Long id, @Valid @RequestBody DriverRequest request) {
        return ResponseEntity.ok(driverService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody DriverStatusRequest request) {
        return ResponseEntity.ok(driverService.updateStatus(id, request));
    }
}
