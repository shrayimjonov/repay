package uz.nodir.file.model.dto.location.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.file.model.entity.Location;
import uz.repay.common.model.dto.response.annotations.LoanId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationBaseResponseDTO {
    @JsonProperty("id")
    @Schema(name = "id", example = "435", description = "location id in database")
    private Long id;

    @LoanId
    @JsonProperty("loan_ext_id")
    @Schema(
            name = "loan_ext_id",
            example = "01121158076",
            description = "loan external id"
    )
    private String loanExtId;


    @JsonProperty("latitude")
    @Schema(name = "latitude", example = "44.325745", description = "location latitude")
    private String latitude;

    @JsonProperty("longitude")
    @Schema(name = "longitude", example = "65.246176", description = "location longitude")
    private String longitude;

    @JsonProperty("description")
    @Schema(name = "description", example = "Uyini manzili", description = "location description")
    private String description;

    public LocationBaseResponseDTO(Location location) {
        this.id = location.getId();
        this.loanExtId = location.getLoanExtId();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.description = location.getDescription();
    }
}
