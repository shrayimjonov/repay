package uz.nodir.repayautoconfigure.service;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.repay.common.model.dto.response.core.SimpleResponse;


@Component
@Slf4j
public class RecordToClassConverter {
    private final ObjectMapper mapper = new ObjectMapper();
    private final static JsonMapper jsonMapper;

    static {
        jsonMapper = JsonMapper
                .builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .build();
    }

    public <T> SimpleResponse<T> convertMessage(String message, Class<T> clazz) {
        try {
            return new SimpleResponse<>(mapper.readValue(message, clazz));
        } catch (Exception e) {
            log.error("mapper converting error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    public <T> SimpleResponse<T> convertMessageV2(String message, Class<T> clazz) {
        try {
            return new SimpleResponse<>(jsonMapper.readValue(message, clazz));
        } catch (Exception e) {
            log.error("mapper converting error: {}", e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }
}
