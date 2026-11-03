package uz.nodir.notification.model.dto.protocol.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.dto.protocol.request.params.ProtocolFilterParam;
import uz.repay.common.model.dto.request.core.params.PageParams;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProtocolFilterRequestDTO extends PageParams {
    @JsonProperty("params")
    @Schema(name = "params", requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "params for filtering messages")
    private ProtocolFilterParam params;


}
