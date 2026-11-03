package uz.repay.common.utils.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateDefaultDeSerializer extends JsonDeserializer<LocalDate> implements ContextualDeserializer {

    private final DateTimeFormatter formatter;

    public DateDefaultDeSerializer() {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Default pattern
    }

    public DateDefaultDeSerializer(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        String date = jsonParser.getText();
        log.info("Received deserialize date: {} by pattern: {}", date, formatter.toString());
        return LocalDate.parse(date, formatter);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        if (property != null) {
            JsonFormat jsonFormat = property.getAnnotation(JsonFormat.class);
            if (jsonFormat != null && !jsonFormat.pattern().isEmpty()) {
                return new DateDefaultDeSerializer(jsonFormat.pattern());
            }
        }
        return this;
    }
}
