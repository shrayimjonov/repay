package uz.nodir.scoring.model.dto.limit.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.params.DebitCardParams;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardLimitRequestDTO {

    @JsonProperty("params")
    @Schema(name = "params",
            example = """
                    {
                                    "type": "pan",
                                    "card": "ENCRYPTED_CARD_TOKEN",
                                    "processing": "HUMO"
                                }""",
            description = "card params")
    @NotNull
    private DebitCardParams params;

}
