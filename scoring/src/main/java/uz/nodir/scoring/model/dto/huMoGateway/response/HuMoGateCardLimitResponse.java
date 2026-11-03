package uz.nodir.scoring.model.dto.huMoGateway.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.utils.serializer.LocalDateTimeDeSerializer;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuMoGateCardLimitResponse {

    @JsonProperty("Body")
    private HuMoGateCardLimitBody body;

    @JsonProperty("error")
    private CoreError error;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HuMoGateCardLimitBody {
        @JsonProperty("getLimitsResponse")
        private HuMoGateCardLimitResult result;
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HuMoGateCardLimitResult {
        @JsonProperty("limit_event")
        private HuMoGateCardLimitEvent event;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HuMoGateCardLimitEvent {

        @JsonProperty("key_type")
        private String keyType;

        @JsonProperty("key_value")
        private String keyValue;

        @JsonProperty("code_owner")
        private String codeOwner;

        @JsonProperty("limit")
        private List<HuMoGateCardLimit> limits;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HuMoGateCardLimit {

        @JsonProperty("subject_value")
        private String subjectValue;

        @JsonProperty("limit_type")
        private String limitType;

        @JsonProperty("limit_value")
        private Long limitValue;

        @JsonProperty("limit_name")
        private String limitName;

        @JsonProperty("limit_date_from")
        private String limitDateFrom;

        @JsonDeserialize(using = LocalDateTimeDeSerializer.class)
        @JsonFormat(pattern = "yyyyMMddHHmmss")
        @JsonProperty("limit_date_to")
        private LocalDateTime limitDateTo;
    }
}
