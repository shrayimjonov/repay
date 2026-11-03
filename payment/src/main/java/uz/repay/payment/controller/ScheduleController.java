package uz.repay.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.service.business.component.ScheduleComponent;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
@Profile("prod")
public class ScheduleController {
    private final ScheduleComponent scheduleComponent;

    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    @PatchMapping("/handleTransactions")
    public ResponseEntity<?> handleRecipientSuccessTransactions() {
        scheduleComponent.handleRecipientSuccessTransactions();
        return ResponseEntity.ok(
                ResultData.ok(true)
        );
    }

    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    @PatchMapping("/handleHuMoDebitConfirms")
    public ResponseEntity<?> handleHuMoDebitConfirms() {
        scheduleComponent.handleHuMoDebitConfirms();
        return ResponseEntity.ok(
                ResultData.ok(true)
        );
    }

    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ROLE_ADMIN)")
    @PatchMapping("/handleNeedConfirm")
    public ResponseEntity<?> handleNeedConfirms() {
        scheduleComponent.handleNeedConfirms();
        return ResponseEntity.ok(
                ResultData.ok(true)
        );
    }
}
