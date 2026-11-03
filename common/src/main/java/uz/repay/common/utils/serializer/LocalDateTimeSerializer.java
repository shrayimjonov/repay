package uz.repay.common.utils.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> implements ContextualSerializer {

    private final DateTimeFormatter formatter;

    public LocalDateTimeSerializer() {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    public LocalDateTimeSerializer(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String formattedDate = localDateTime.format(formatter);
        log.info("Serialized date: {} using pattern: {}", formattedDate, formatter);
        jsonGenerator.writeString(formattedDate);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) {
        if (property != null) {
            JsonFormat jsonFormat = property.getAnnotation(JsonFormat.class);
            if (jsonFormat != null && !jsonFormat.pattern().isEmpty()) {
                return new LocalDateTimeSerializer(jsonFormat.pattern());
            }
        }
        return this;
    }
}
