package uz.repay.common.utils.serializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.utils.DateUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateDefaultSerializer extends JsonSerializer<LocalDate> implements ContextualSerializer {

    private final DateTimeFormatter formatter;

    public DateDefaultSerializer() {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Default pattern
    }

    public DateDefaultSerializer(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String formattedDate = localDate.format(formatter);
        log.info("Serialized date: {} using pattern: {}", formattedDate, formatter);
        jsonGenerator.writeString(formattedDate);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) {
        if (property != null) {
            JsonFormat jsonFormat = property.getAnnotation(JsonFormat.class);
            if (jsonFormat != null && !jsonFormat.pattern().isEmpty()) {
                return new DateDefaultSerializer(jsonFormat.pattern());
            }
        }
        return this;
    }
}
