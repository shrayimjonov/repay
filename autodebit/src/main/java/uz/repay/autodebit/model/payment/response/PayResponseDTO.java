package uz.repay.autodebit.model.payment.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.ResponseStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayResponseDTO {
    private Long id;
    private Branch branch;
    private String cardOwner;
    private ResponseStatus status;
    private String refNum;
    private String note;
    private Long amount;
    private String reverseReason;
    private String loanExtId;
    private String cardPan;
    private AddType payType;
    private LocalDateTime initTime;

}
