package uz.repay.card.model.dto.core.response.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPResponseDTO {
    @JsonProperty("phone")
    @Schema(name = "phone", example = "******9989", description = "masked phone number")
    private String phone;

    @JsonProperty("id")
    @Schema(name = "id", example = "1719813134295", description = "generated random string")
    private String id;

    @JsonProperty("type")
    @Schema(name = "type", example = "UZCARD", description = "processing type")
    private Processing type;

    @JsonProperty("isConfirmed")
    @Schema(name = "isConfirmed", example = "true", description = "Card if is confirmed call familiar API else not familiar API")
    private Boolean isConfirmed;

    public OTPResponseDTO(String id, String phone, Processing processing) {
        this.phone = phone;
        this.id = id;
        this.type = processing;
        this.isConfirmed = false;
    }
}
