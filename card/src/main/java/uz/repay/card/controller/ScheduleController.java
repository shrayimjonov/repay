package uz.repay.card.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.card.service.business.component.CardAutoAdderComponent;
import uz.repay.card.service.business.component.EPosReconciliationComponent;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResponseStatus;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final CardAutoAdderComponent cardAutoAdderComponent;
    private final EPosReconciliationComponent ePosReconciliationComponent;

    @PatchMapping("/findAllCards")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    public ResponseEntity<?> findAllLoansCards() {
        cardAutoAdderComponent.findAllLoansCards();
        return ResponseEntity.ok(
                ResultData.ok(true)
        );
    }

    @PatchMapping("/reconcile")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    public ResponseEntity<?> reconciliationHuMoTerminals() {
        ePosReconciliationComponent.reconciliationHuMoTerminals();
        return ResponseEntity.ok(
                ResultData.ok(true)
        );
    }

    @PatchMapping("/findAutoPayCards")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    public ResponseEntity<?> findAutoPayCards() {
        cardAutoAdderComponent.findAutoPayCards();
        return ResponseEntity.ok(
                ResultData.ok(
                        new CoreError(0, ResponseStatus.SUCCESS.name())
                )
        );
    }

}
