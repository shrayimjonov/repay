package uz.repay.common.model.dto.request.loan.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.core.params.DateRangeParams;
import uz.repay.common.model.enums.Branch;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanSearchParams {
    @Schema(name = "personCode", example = "30000000000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "client PINFL")
    private String personCode;
    @Schema(name = "extId", example = "0098127386", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "loan external id in core banking")
    private String extId;
    @Schema(name = "name", example = "ESHMATOV TOSHMAT TESHABOY O'G'LI", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "client full name")
    private String name;
    @Schema(name = "phone", example = "998901234567", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "client phone number")
    private String phone;
    @Schema(name = "clientId", example = "99198810", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "client unique code in core banking")
    private String clientId;
    @Schema(name = "isConvicted", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "find convicted client or not")
    private Boolean isConvicted;
    @Schema(name = "branches", example = "[00981,01046]", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "required branches list")
    private List<Branch> branches;
    @Schema(name = "rangeParams", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Loans created range period")
    private DateRangeParams rangeParams;
    @Schema(name = "isFeatured", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Take featured loans")
    private Boolean isFeatured;
    @Schema(name = "isOwnLoans", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Take only own loans")
    private Boolean isOwnLoans;
    @Schema(name = "is_insolvent", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Take only insolvent loans")
    @JsonProperty("is_insolvent")
    private Boolean isInsolvent;
}
