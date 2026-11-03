package nodir.paymo_add.model;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Component
public class DateDeserializer {

    @SneakyThrows
    public String dateDeserialize(String date) {
        String day = date.substring(8);
        String month = date.substring(5,7);
        String year = date.substring(0,4);

        return day + "." + month + "." + year;
    }


}
