package uz.repay.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.payment.model.dto.core.pcMaintenance.request.PCMaintenanceAddSettingsRequestDTO;
import uz.repay.payment.service.business.PCMaintenanceService;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).PC_SETTER)")
public class SettingsController {
    private final PCMaintenanceService service;

    @GetMapping("/{id}")
    public ResponseEntity<?> findSettingByPC(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findSettingByPC(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> addSettingsToPC(@PathVariable("id") Long id, @RequestBody @Valid PCMaintenanceAddSettingsRequestDTO requestDTO) {
        return ResponseEntity.ok(service.addABSSettingToPCMaintenance(id, requestDTO));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid PCMaintenanceAddSettingsRequestDTO requestDTO) {
        return ResponseEntity.ok(service.update(id, requestDTO));
    }

    @DeleteMapping("/{pcMId}/{settingId}")
    public ResponseEntity<?> deleteABSSettingById(@PathVariable("pcMId") Long pcMId, @PathVariable("settingId") Long settingId) {
        return ResponseEntity.ok(service.deleteABSSettingById(pcMId, settingId));
    }

}
