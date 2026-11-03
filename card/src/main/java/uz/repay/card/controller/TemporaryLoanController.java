package uz.repay.card.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.card.service.business.component.OneTimeCardFinderComponent;
import uz.repay.card.service.core.TemporaryLoanDAO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResponseStatus;

@RestController
@RequestMapping("/api/v1/loanTemporary")
@RequiredArgsConstructor
public class TemporaryLoanController {
    private final OneTimeCardFinderComponent oneTimeCardFinderComponent;
    private final TemporaryLoanDAO temporaryLoanDAO;

    @PostMapping
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ONE_TIME_CARD_FINDER)")
    public ResponseEntity<?> oneTimeLoanTemporaryCardFinder() {
        oneTimeCardFinderComponent.cardFind();
        return ResponseEntity.ok(
                ResultData.ok(
                        new CoreError(0, ResponseStatus.SUCCESS.name())
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).ONE_TIME_CARD_FINDER)")
    public ResponseEntity<?> get() {

        return ResponseEntity.ok(
                ResultData.ok(
                        temporaryLoanDAO.findAll())
                );
    }
}
