package uz.repay.payment.model.dto.nciBilling.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.utils.CommonUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NCICheckResponse<R> {
    private Integer code;
    private ResponseStatus status;
    private R result;
    private NCIError error;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NCIError {
        private Integer code;
        private String message;

        public String getMessage() {
            if (message == null)
                return null;
            return CommonUtils.removeNonUtf8CompliantCharacters(message);
        }
    }
}
