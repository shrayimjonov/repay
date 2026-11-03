package uz.repay.payment.model.dto.humo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HuMoGetPaymentByIdRequest {
    private String login;
    private String paymentId;
    private String subSystem;
}
