package uz.repay.common.model.dto.request.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.payment.params.NotificationParams;
import uz.repay.common.model.dto.response.annotations.LoanId;
import uz.repay.common.model.dto.response.core.params.DebitCardParams;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Payer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayRequestDTO {
    @LoanId
    @Schema(name = "loanExtId", example = "0098127386", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "\\d{9,}")
    private String loanExtId;
    @NotNull
    @Min(1)
    @Schema(name = "amount", example = "50100", requiredMode = Schema.RequiredMode.REQUIRED, description = "Amount is in tiyin. Means 50000 = 500 sum")
    private Long amount = 0L;
    @Valid
    @NotNull
    @Schema(name = "debitParams", example = "{\n" +
            "        \"type\" : \"token\",\n" +
            "        \"card\" : \"6BE40F1C1C344B85BFDA9683F2C96B34\",\n" +
            "        \"processing\" : \"UZCARD\"\n" +
            "    }", requiredMode = Schema.RequiredMode.REQUIRED, description = "Debiting from card by this params")
    private DebitCardParams debitParams;
    @NotNull
    @Valid
    @Schema(name = "params", example = "{\n" +
            "        \"cardPan\" : \"561468******7308\",\n" +
            "        \"cardOwner\" : \"N SH SIROJIDDINOV\",\n" +
            "        \"payer\" : \"BORROWER\"\n" +
            "    }", requiredMode = Schema.RequiredMode.REQUIRED, description = "Save card info to database nothing else")
    private PayParams params;
    @NotNull
    @Schema(name = "addType", example = "MANUAL", requiredMode = Schema.RequiredMode.REQUIRED, description = "Debiting manually or automatically param")
    private AddType addType;
    @NotNull
    @Schema(name = "notificationParams", example = "{\n" +
            "        \"phone\" : \"998901234567\",\n" +
            "        \"fullName\" : \"SIROJIDDINOV NODIRBEK\",\n" +
            "        \"loanId\" : 85600\n" +
            "    }", requiredMode = Schema.RequiredMode.REQUIRED, description = "Notification data for sending message")
    private NotificationParams notificationParams;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PayParams {
        @NotBlank
        @Schema(name = "cardPan", example = "860051******0293", requiredMode = Schema.RequiredMode.REQUIRED, description = "Card masked pan")
        private String cardPan;
        @NotBlank
        @Schema(name = "cardOwner", example = "SIROJIDDINOV NODIRBEK SH", requiredMode = Schema.RequiredMode.REQUIRED, description = "Card holder name")
        private String cardOwner;
        @NotNull
        @Schema(name = "payer", example = "GUARANTOR", requiredMode = Schema.RequiredMode.REQUIRED, description = "Card holder type")
        private Payer payer;
    }


}
