package uz.nodir.notification.model.dto.core.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.dto.core.TextProp;
import uz.nodir.notification.model.entity.Template;
import uz.nodir.notification.model.enums.TemplateType;
import uz.repay.common.model.enums.Language;
import uz.repay.common.model.enums.UserPermission;

import java.util.HashMap;

@Data
@NoArgsConstructor
public class TemplateTypeResponseDTO {
    @JsonProperty("type")
    @Schema(name = "type", example = "CUSTOM", description = "template type")
    private TemplateType type;

    @JsonProperty("is_usable")
    @Schema(name = "is_usable", example = "true", description = "user can uses only types with true value")
    private Boolean isUsable;

    @JsonProperty("role")
    @Schema(name = "role", example = "REPAY_MIB_SMS", description = "Template type using only for this role")
    private UserPermission role;

    @JsonProperty("preview")
    @Schema(name = "preview", example = "{\n" +
            "                \"RU\": {\n" +
            "                    \"text\": \"По анкете #, привязанной к Вам, с карты # было снято # сум.\",\n" +
            "                    \"valuesCount\": 3,\n" +
            "                    \"valueType\": {\n" +
            "                        \"1\": \"LOAN\",\n" +
            "                        \"2\": \"CARD\",\n" +
            "                        \"3\": \"AMOUNT\"\n" +
            "                    }\n" +
            "                },\n" +
            "                \"EN\": {\n" +
            "                    \"text\": \"According to the loan # linked to you, from card # was withdrawn # sum.\",\n" +
            "                    \"valuesCount\": 3,\n" +
            "                    \"valueType\": {\n" +
            "                        \"1\": \"LOAN\",\n" +
            "                        \"2\": \"CARD\",\n" +
            "                        \"3\": \"AMOUNT\"\n" +
            "                    }\n" +
            "                },\n" +
            "                \"UZ\": {\n" +
            "                    \"text\": \"Sizga bog'langan # raqamli kredit anketasi uchun # kartadan # sum yechib olindi.\",\n" +
            "                    \"valuesCount\": 3,\n" +
            "                    \"valueType\": {\n" +
            "                        \"1\": \"LOAN\",\n" +
            "                        \"2\": \"CARD\",\n" +
            "                        \"3\": \"AMOUNT\"\n" +
            "                    }\n" +
            "                }\n" +
            "            }", description = "Preview of sending text")
    private HashMap<Language, TextProp> preview = new HashMap<>();


    @JsonProperty("description")
    @Schema(name = "description", description = "template description", example = "Пользовательское")
    private String description;

    public TemplateTypeResponseDTO(TemplateType type) {
        this.type = type;
        this.isUsable = type.getIsUsable();
        this.role = type.getRole();
    }

    public TemplateTypeResponseDTO(TemplateType z, Template template) {
        this.type = z;
        this.isUsable = z.getIsUsable();
        this.role = z.getRole();
        if (template != null)
            this.preview = template.getTextValues();
        this.description = z.getDescription();

    }
}
