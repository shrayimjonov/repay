package uz.repay.loan.model.dto.nciGateway.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.loan.utils.BankDayDateDeserializer;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NCIBankDayResponse {
    @JsonProperty("union_id")
    private String union_id;
    @JsonProperty("bank_id")
    private String bank_id;
    @JsonProperty("short_name")
    private String short_name;
    @JsonProperty("curr_date")
    @JsonDeserialize(using = BankDayDateDeserializer.class)
    private LocalDate curr_date;
    @JsonProperty("status_open")
    private String status_open;
    @JsonProperty("status_close")
    private String status_close;
    @JsonProperty("process")
    private String process;
}
