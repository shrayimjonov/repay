package uz.repay.card.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.repay.card.model.dto.core.request.reco.ReconciliationFilterRequestDTO;
import uz.repay.card.service.business.ReconciliationService;

@RestController
@RequestMapping("/api/v1/reconciliation")
@RequiredArgsConstructor
public class ReconciliationController {
    private final ReconciliationService reconciliationService;

    @PostMapping("/filter")
    public ResponseEntity<?> filter(@RequestBody @Valid ReconciliationFilterRequestDTO requestDTO) {
        return ResponseEntity.ok(reconciliationService.filter(requestDTO));
    }
}
