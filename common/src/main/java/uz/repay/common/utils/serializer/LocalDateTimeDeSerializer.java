package uz.repay.common.utils.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LocalDateTimeDeSerializer extends JsonDeserializer<LocalDateTime> implements ContextualDeserializer {

    private final DateTimeFormatter formatter;

    public LocalDateTimeDeSerializer() {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); // Default pattern
    }

    public LocalDateTimeDeSerializer(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        String date = jsonParser.getText();
        log.info("Received deserialize date: {} by pattern: {}", date, formatter.toString());
        return LocalDateTime.parse(date, formatter);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        if (property != null) {
            JsonFormat jsonFormat = property.getAnnotation(JsonFormat.class);
            if (jsonFormat != null && !jsonFormat.pattern().isEmpty()) {
                return new LocalDateTimeDeSerializer(jsonFormat.pattern());
            }
        }
        return this;
    }
}
