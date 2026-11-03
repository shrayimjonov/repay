package uz.repay.payment.model.dto.billing.response.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataResult<D> {
    private String name;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private Object commission;
    private Integer currency;
    private D data;

}
