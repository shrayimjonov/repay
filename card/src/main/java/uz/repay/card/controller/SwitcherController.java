package uz.repay.card.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.card.model.dto.core.request.switcher.SwitcherPatchDTO;
import uz.repay.card.service.core.SwitcherRedisService;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/switcher")
@RequiredArgsConstructor
@PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
public class SwitcherController {
    private final SwitcherRedisService service;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(new ResultData<>(service.findAll()));
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> turnOnOrOff(@PathVariable("id") UUID id, @RequestBody @Valid SwitcherPatchDTO dto) {
        return ResponseEntity.ok(new ResultData<>(service.updateStatus(id, dto.getIsEnabled())));
    }

}
