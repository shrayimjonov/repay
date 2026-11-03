package uz.nodir.notification.model.dto.push.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushV2ResponseDTO {
    @JsonProperty("id")
    @Schema(name = "id", example = "18495c68-2fa0-4813-b1c4-5c8b2463212b", description = "id field")
    private UUID id;
    @JsonProperty("text")
    @Schema(name = "text", example = "Sizga ulangan 009813123 raqamli anketaga 300 000 so'm yechildi!", description = "text field")
    private String text;
    @JsonProperty("is_read")
    @Schema(name = "is_read", example = "true", description = "User read this push or not")
    private Boolean isRead;


}
