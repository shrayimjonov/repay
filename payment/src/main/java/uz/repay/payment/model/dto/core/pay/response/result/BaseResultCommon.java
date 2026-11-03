package uz.repay.payment.model.dto.core.pay.response.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseResultCommon {
    private Map<Object, Object> request;
    private Map<Object, Object> response;
    private Boolean isSuccess = false;
}
