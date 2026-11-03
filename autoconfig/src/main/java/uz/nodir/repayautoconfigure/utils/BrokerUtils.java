package uz.nodir.repayautoconfigure.utils;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;


@Slf4j
public class BrokerUtils {
    private final static JsonMapper jsonMapper;

    static {
        jsonMapper = JsonMapper
                .builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .build();
    }

    public static <T> SimpleResponse<T> check(Object data, Class<T> clazz) {
        if (!clazz.isInstance(data)) {

            if (clazz.equals(String.class)) {
                return castDataToObject(data);
            }

            var message = "Data is not of type: %s".formatted(clazz.getSimpleName());
            log.error("Exit with error: {}", message);
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        }

        T castedData = clazz.cast(data);
        return new SimpleResponse<>(castedData);
    }

    @SuppressWarnings("unchecked")
    private static <T> SimpleResponse<T> castDataToObject(Object data) {
        try {
            var jsonData = jsonMapper.writeValueAsString(data);
            return (SimpleResponse<T>) new SimpleResponse<>(jsonData);
        } catch (Exception e) {
            log.error("Exit with error: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), e.getMessage());
        }
    }
}
