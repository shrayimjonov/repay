package uz.repay.payment.model.dto.core.pay.reqeuest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReverseRequestDTO {
    @Min(1)
    private Long tranId = 0L;
    @NotBlank
    private String reason;
}
