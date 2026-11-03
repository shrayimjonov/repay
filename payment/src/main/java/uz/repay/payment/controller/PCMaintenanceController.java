package uz.repay.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.payment.model.dto.core.pcMaintenance.request.PCMaintenanceRequestDTO;
import uz.repay.payment.service.business.PCMaintenanceService;

@RestController
@RequestMapping("/api/v1/pc")
@RequiredArgsConstructor
@PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).PC_SETTER)")
public class PCMaintenanceController {
    private final PCMaintenanceService service;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid PCMaintenanceRequestDTO requestDTO) {
        return ResponseEntity.ok(service.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid PCMaintenanceRequestDTO requestDTO) {
        return ResponseEntity.ok(service.update(id, requestDTO));
    }

    @PatchMapping("/cacheEvict")
    public ResponseEntity<?> cacheEvict() {
        return ResponseEntity.ok(service.cacheEvict());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

}
