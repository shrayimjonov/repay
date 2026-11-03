package uz.repay.common.model.dto.response.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import uz.repay.common.model.enums.ResultState;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleResponse<S> {
    private CoreError error;
    private Boolean status;
    private S data;
    private Map<Object, Object> params = new HashMap<>(5);
    private Exception exception;
    private boolean isTimeout = false;

    public SimpleResponse() {
        this.status = true;
    }

    public SimpleResponse(S data) {
        this.status = true;
        this.data = data;
    }

    public SimpleResponse(Map<Object, Object> params, Boolean isSuccess) {
        this.status = isSuccess;
        this.params = params;
    }

    public SimpleResponse(Integer id, String message) {
        this.status = false;
        this.error = new CoreError(id, message);
    }

    public SimpleResponse(String message, boolean isTimeout) {
        this.status = false;
        this.isTimeout = isTimeout;
        this.error = new CoreError(ResultState.TimeOutError.getCode(), message);
    }

    public SimpleResponse(Integer id, String message, Exception ex) {
        this.status = false;
        this.error = new CoreError(id, message);
        this.exception = ex;
    }

    public Map<Object, Object> toMap() {
        var entityMap = new HashMap<>();
        entityMap.put("error", this.error);
        entityMap.put("status", this.status);
        entityMap.put("data", this.data);
        entityMap.put("params", this.params);
        return entityMap;
    }
}
