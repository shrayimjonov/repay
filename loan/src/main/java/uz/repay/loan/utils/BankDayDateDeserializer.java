package uz.repay.loan.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class BankDayDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        var format = "yyyy-MM-dd hh:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        log.info("Date time formatter: {}", formatter);

        log.info("Taken text: {}", jsonParser.getText());
        String date = jsonParser.getText();
        var result = LocalDate.parse(date, formatter);

        log.info("Result is: {}", result);
        return result;
    }
}
