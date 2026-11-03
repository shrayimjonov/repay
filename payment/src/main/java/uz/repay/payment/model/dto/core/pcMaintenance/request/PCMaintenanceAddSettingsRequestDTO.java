package uz.repay.payment.model.dto.core.pcMaintenance.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.Branch;
import uz.repay.common.model.enums.Currency;
import uz.repay.payment.model.enums.ABSDocType;
import uz.repay.payment.model.enums.ABSPayType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PCMaintenanceAddSettingsRequestDTO {
    @NotNull
    @Min(1)
    private Long pcMaintenanceId = 0L;
    @Branch
    private String branchDebit;
    @NotBlank
    @Pattern(regexp = "(^\\d{20}$)|(EPOS_ACC)")
    private String accountDebit;
    @Branch
    private String branchCredit;
    @NotBlank
    @Pattern(regexp = "(^\\d{20}$)|(EPOS_ACC)")
    private String accountCredit;
    @NotNull
    private Currency currency;
    @NotBlank
    private String purpose;
    @NotBlank
    private String purposeCode;
    @NotNull
    @Min(1)
    private Integer order = 0;
    @NotNull
    private ABSDocType docType;
    @NotNull
    private ABSPayType payType;
}
