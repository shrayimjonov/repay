package uz.repay.loan.model.dto.core.loan.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Language;
import uz.repay.loan.model.dto.core.guarantor.response.GuarantorShortResult;
import uz.repay.loan.model.entity.Loan;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanClientInfoResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("person_code")
    private String personCode;

    @JsonProperty("ext_id")
    private String extId;

    @JsonProperty("payer_name")
    private String payerName;

    @JsonProperty("payer_phone")
    private String payerPhone;

    @JsonProperty("id_nibbd")
    private String idNibbd;

    @JsonProperty("is_convicted")
    private Boolean isConvicted;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("is_auto_debit")
    private Boolean isAutoDebit;

    @JsonProperty("is_featured")
    private Boolean isFeatured;

    @JsonProperty("language")
    private Language language;

    @JsonProperty("is_insolvent")
    private Boolean isInsolvent;

    @JsonProperty("guarantors")
    private List<GuarantorShortResult> guarantors = new ArrayList<>();

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("branch")
    private Branch branch;

    public LoanClientInfoResponseDTO(Loan loan) {
        this.id = loan.getId();
        this.personCode = loan.getPinfl();
        this.extId = loan.getExtId();
        this.payerName = loan.getPayerName();
        this.payerPhone = loan.getPayerPhone();
        this.idNibbd = loan.getIdNibbd();
        this.isConvicted = loan.getIsConvicted();
        this.createdBy = loan.getCreatedBy();
        this.isAutoDebit = loan.getIsAutoDebit();

        String username = SecurityUtils.getUsername();
        if (!username.equals("system") && !username.equals("external"))
            this.isFeatured = loan.getFeaturedLoans().stream().anyMatch(z -> z.getCreatedBy().equals(username));

        this.language = loan.getLanguage();
        this.isInsolvent = loan.getIsInsolvent();
        this.guarantors = new ArrayList<>(
                loan.getGuarantors()
                        .stream()
                        .map(GuarantorShortResult::new)
                        .toList()
        );
        this.productName = loan.getProductName();
        this.branch = loan.getBranch();
    }
}
