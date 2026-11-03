package uz.repay.common.model.dto.response.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ResultData<R> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer successCode;
    private Boolean isSuccess;
    private ResponseStatus status;
    private R result;
    private CoreError error;
    private Long timeStamp = System.currentTimeMillis();

    public ResultData(R result) {
        this.successCode = 0;
        this.isSuccess = true;
        this.status = ResponseStatus.SUCCESS;
        this.result = result;
    }

    public ResultData(Integer code, Exception e) {
        this.successCode = -1;
        this.isSuccess = false;
        this.status = ResponseStatus.FAILED;
        this.error = new CoreError(code, e.getMessage());
    }

    public ResultData(Integer code, String message) {
        this.successCode = -1;
        this.isSuccess = false;
        this.status = ResponseStatus.FAILED;
        this.error = new CoreError(code, message);
    }


    public ResultData(ResultState resultState, String message) {
        this.successCode = -1;
        this.isSuccess = false;
        this.status = ResponseStatus.FAILED;
        this.error = new CoreError(resultState.getCode(), message);
    }

    public ResultData(CoreError coreError, Boolean isSuccess) {
        this.successCode = -1;
        this.isSuccess = false;
        this.status = ResponseStatus.FAILED;
        this.error = coreError;
    }

    public static <T> ResultData<T> ok(@NotNull T body) {
        return new ResultData<>(body);
    }

    public static ResultData<CoreError> error(@NotNull ResultState resultState, @NotNull String message) {
        return new ResultData<>(resultState, message);
    }

    public Map<Object, Object> toMap() {
        var map = new HashMap<>();
        map.put("successCode", this.successCode);
        map.put("isSuccess", this.isSuccess);
        map.put("status", this.status);

        if (this.result != null)
            map.put("result", this.result);
        if (this.error != null)
            map.put("error", this.error);

        return map;
    }

    public String errorToJson() {
        return "{\n" +
                "    \"successCode\": " + this.successCode + ",\n" +
                "    \"isSuccess\": " + this.isSuccess + ",\n" +
                "    \"status\": \"" + this.status + "\",\n" +
                "    \"error\": {\n" +
                "        \"code\": " + this.error.getCode() + ",\n" +
                "        \"message\": \"" + this.error.getMessage() + "\"\n" +
                "    },\n" +
                "    \"timeStamp\": " + this.timeStamp + "\n" +
                "}";
    }
}
