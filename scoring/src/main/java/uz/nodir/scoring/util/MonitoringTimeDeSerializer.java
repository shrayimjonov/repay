package uz.nodir.scoring.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.exception.InvalidDataException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class MonitoringTimeDeSerializer extends JsonDeserializer<LocalTime> {
    @Override
    public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        String pattern = "HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        try {
            var time = jsonParser.getText();

            log.info("Received deserialize time: {} by pattern: {}", time, pattern);
            return LocalTime.parse(time, formatter);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new InvalidDataException(e.getMessage());
        }
    }
}
