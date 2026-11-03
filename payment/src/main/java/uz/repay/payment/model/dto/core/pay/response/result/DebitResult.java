package uz.repay.payment.model.dto.core.pay.response.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.ResponseStatus;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DebitResult extends BaseResultCommon {

    private ErrorResult error;
    private String rrn;
    private String paymentID;
    private Integer resultCode = -1;

    public DebitResult(ErrorResult error) {
        this.resultCode = -1;
        this.setIsSuccess(false);
        this.error = error;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResult {
        private String message;
        private ResponseStatus status;
    }
}
