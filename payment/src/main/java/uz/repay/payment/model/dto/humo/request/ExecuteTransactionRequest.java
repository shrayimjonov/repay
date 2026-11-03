package uz.repay.payment.model.dto.humo.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class ExecuteTransactionRequest {
    @JsonAlias({"PAYMENT_MODE", "payment_mode"})
    private Integer paymentMode = 3;

    @JsonAlias({"CARD"})
    @Pattern(regexp = "\\d{16}")
    private String card;

    @JsonAlias({"ACCOUNT_NO", "account_no"})
    private String accountNo;

    @JsonAlias({"TRAN_TYPE", "tran_type"})
    private String tranType;

    @JsonAlias({"TRAN_CCY", "tran_ccy"})
    @Pattern(regexp = "\\d{3}")
    private String tranCcy = "860";

    @JsonAlias({"TRAN_AMNT", "tran_amnt"})
    @NotNull
    @Min(1)
    private Long tranAmnt;

    @JsonAlias({"BANK_C", "bank_c"})
    private String bankC = "25";

    @JsonAlias({"GROUPC", "groupC", "group_c"})
    private String groupc = "01";

    @JsonAlias({"DEAL_DESC", "deal_desc"})
    private String dealDesc;

    @JsonAlias({"EXTERNAL_SESSION_ID", "external_session_id"})
    private String externalSessionId;

    private String subSystem = "PAYMO";
}
