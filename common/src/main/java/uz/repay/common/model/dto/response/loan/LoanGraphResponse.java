package uz.repay.common.model.dto.response.loan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanGraphResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("oper_id")
    private String operationId;
    @JsonProperty("exp_id")
    private String expId;
    @JsonProperty("v_date")
    private LocalDate vDate;
    @JsonProperty("summa")
    private Long amount;
    @JsonProperty("status")
    private String status;
    @JsonProperty("num")
    private String num;
    @JsonProperty("repay")
    private String repay;
}
