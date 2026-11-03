package uz.repay.autodebit.model.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.autodebit.service.business.implV2.PaymentWorkerV2;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkersResponseDTO {
    private String branch;
    private UUID id;
    private Boolean isFinished;
    private boolean isInsolvent;

    public WorkersResponseDTO(PaymentWorkerV2 paymentWorkerV2) {
        this.branch = paymentWorkerV2.getBranch();
        this.id = paymentWorkerV2.getWorkerId();
        this.isFinished = paymentWorkerV2.isFinished();
        this.isInsolvent = paymentWorkerV2.isInsolvent();
    }
}
