package uz.repay.card.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.card.model.dto.core.request.epos.EPosCreateRequestDTO;
import uz.repay.card.model.dto.core.request.epos.EPosExceptionalDTO;
import uz.repay.card.service.business.EPosService;

@RestController
@RequestMapping("/api/v1/ePos")
@RequiredArgsConstructor
public class EPosController {
    private final EPosService ePosService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid EPosCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(ePosService.create(requestDTO));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(ePosService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid EPosCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(ePosService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ePosService.delete(id));
    }


    @PatchMapping("/cacheEvict")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    public ResponseEntity<?> cacheEvict() {
        return ResponseEntity.ok(ePosService.evictCache());
    }

    @PatchMapping
    public ResponseEntity<Void> makeExceptional(@RequestBody EPosExceptionalDTO requestDTO) {
        ePosService.makeExceptional(requestDTO);
        return ResponseEntity.ok().build();
    }
}
