package uz.repay.payment.model.dto.core.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import uz.repay.common.model.enums.Processing;

public record EposCheckDTO(
        Processing processing,
        String branch,
        @JsonProperty(value = "forTransPay", defaultValue = "false")
        boolean forTransPay
) {
}
