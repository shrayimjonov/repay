package uz.nodir.scoring.model.dto.history.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.params.DebitCardParams;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardHistoryRequestDTO {
    @JsonProperty("params")
    @Schema(name = "params",
            example = "{\n" +
            "                \"type\": \"pan\",\n" +
            "                \"card\": \"ENCRYPTED_CARD_TOKEN\",\n" +
            "                \"processing\": \"HUMO\"\n" +
            "            }",
    description = "card params")
    private DebitCardParams params;
}
