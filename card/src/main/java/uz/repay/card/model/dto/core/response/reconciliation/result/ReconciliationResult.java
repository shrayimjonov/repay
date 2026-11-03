package uz.repay.card.model.dto.core.response.reconciliation.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.entity.Reconciliation;
import uz.repay.common.model.enums.ResponseStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationResult {
    private Long id;
    private String refNum;
    private String terminalId;
    private String paymentId;
    private Integer creditsReversalNumber;
    private Long creditsReversalAmount;
    private Integer debitsNumber;
    private Long debitsAmount;
    private ResponseStatus status;
    private LocalDateTime createdTime;

    public ReconciliationResult(Reconciliation reconciliation) {
        this.id = reconciliation.getId();
        this.refNum = reconciliation.getRefNum();
        this.terminalId = reconciliation.getTerminalId();
        this.paymentId = reconciliation.getPaymentId();
        this.creditsReversalNumber = reconciliation.getCreditsReversalNumber();
        this.creditsReversalAmount = reconciliation.getCreditsReversalAmount();
        this.debitsNumber = reconciliation.getDebitsNumber();
        this.debitsAmount = reconciliation.getDebitsAmount();
        this.status = reconciliation.getStatus();
        this.createdTime = reconciliation.getInitTime();
    }
}
