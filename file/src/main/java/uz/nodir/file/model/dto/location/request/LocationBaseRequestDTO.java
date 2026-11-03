package uz.nodir.file.model.dto.location.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.annotations.LoanId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationBaseRequestDTO {
    @LoanId
    @JsonProperty("loan_ext_id")
    @Schema(
            name = "loan_ext_id",
            example = "01121158076",
            requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "\\d{9,}",
            description = "loan external id"
    )
    private String loanExtId;


    @JsonProperty("latitude")
    @Schema(name = "latitude", example = "44.325745", description = "location latitude", requiredMode = Schema.RequiredMode.REQUIRED)
    private String latitude;

    @JsonProperty("longitude")
    @Schema(name = "longitude", example = "65.246176", description = "location longitude", requiredMode = Schema.RequiredMode.REQUIRED)
    private String longitude;

    @JsonProperty("description")
    @Schema(name = "description", example = "Uyini manzili", description = "location description", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;
}
