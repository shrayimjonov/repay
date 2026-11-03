package uz.nodir.notification.model.dto.protocol.request.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.enums.TemplateType;
import uz.repay.common.model.dto.request.core.params.DateRangeParams;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolFilterParam {
    @JsonProperty("phone")
    @Schema(name = "phone", example = "998901234567", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "phone which sent message")
    private String phone;
    @JsonProperty("message")
    @Schema(name = "message", example = "Qarzdorlik mavjud", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "text of message")
    private String message;
    @JsonProperty("types")
    @ArraySchema(
            schema =
            @Schema(
                    name = "types", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "types of sent templates", example = "DEBT_OFF"
            )
    )
    private List<TemplateType> types;
    @JsonProperty("range_params")
    @Schema(name = "range_params", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "date range params")
    private DateRangeParams rangeParams;
}
