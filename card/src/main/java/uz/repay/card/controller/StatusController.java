package uz.repay.card.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.card.model.dto.core.request.status.StatusRequestDTO;
import uz.repay.card.service.business.StatusService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.StatusType;

@RestController
@RequestMapping("/api/v1/status")
@RequiredArgsConstructor
@PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
public class StatusController {
    private final StatusService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid StatusRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestParam("id") Long id, @RequestBody @Valid StatusRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String definitionUz) {
        return ResponseEntity.ok(service.findAll(definitionUz));
    }


    @GetMapping("/types")
    public ResponseEntity<?> getStatusTypes() {
        return ResponseEntity.ok(new ResultData<>(StatusType.values()));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.deleteById(id));
    }

    @PatchMapping("/cacheEvict")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    public ResponseEntity<?> cacheEvict() {
        return ResponseEntity.ok(service.evictCache());
    }
}
