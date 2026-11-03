package uz.repay.payment.model.dto.core.pay.response.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.enums.ResponseStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SyncResult extends BaseResultCommon {
    private ResponseStatus status;
    private String RRN;
    private CoreError error;

}
