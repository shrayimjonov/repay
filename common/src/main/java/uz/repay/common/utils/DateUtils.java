package uz.repay.common.utils;

import lombok.extern.slf4j.Slf4j;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.request.core.params.DateRange;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResultState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Slf4j
public class DateUtils {

    public static LocalDateTime getDateTimeNow() {
        return LocalDateTime.now(ZoneId.of("Asia/Tashkent"));
    }

    public static LocalDate getDateNow() {
        return LocalDate.now(ZoneId.of("Asia/Tashkent"));
    }

    //dd.MM.yyyy -> pattern format
    public static String ddMMyyyyRegex() {
        return "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    }

    public static void validateDateFields(String startDate, String endDate) {
        log.info("Date Fields not empty and will be validated");

        if (!startDate.matches(DateUtils.ddMMyyyyRegex()))
            throw new InvalidDataException("Start Date is not valid: %s".formatted(startDate));

        if (!endDate.matches(DateUtils.ddMMyyyyRegex()))
            throw new InvalidDataException("End Date is not valid: %s".formatted(endDate));

    }

    public static String LocalDateConvertToFormat(LocalDate localDate, String format) {
        try {
            var dtf = DateTimeFormatter.ofPattern(format);
            return dtf.format(localDate);
        } catch (Exception e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    public static void validateDate(DateRange dateRange) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate dateStart;
        LocalDate dateFinish;

        if (dateRange.getDateFrom() == null || dateRange.getDateTo() == null || dateRange.getDateFrom().isEmpty() || dateRange.getDateTo().isEmpty())
            throw new InvalidDataException("Date cannot be null: StartDate: %s EndDate: %s".formatted(dateRange.getDateFrom(), dateRange.getDateTo()));

        try {
            dateStart = LocalDate.parse(dateRange.getDateFrom(), dateFormatter);
        } catch (DateTimeException e) {
            throw new InvalidDataException("Date From catch error: %s".formatted(e.getMessage()));
        }

        try {
            dateFinish = LocalDate.parse(dateRange.getDateTo(), dateFormatter);
        } catch (DateTimeException e) {
            throw new InvalidDataException("Date To catch error: %s".formatted(e.getMessage()));
        }


        if (dateFinish.isBefore(dateStart))
            throw new InvalidDataException("The End date cannot be less than the Start date! %s %s"
                    .formatted(dateStart.getMonth(), dateFinish.getMonth()));

    }

    public static SimpleResponse<LocalDate> strToLocalDate(String dateStr, String format) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
        try {
            var parsedDate = LocalDate.parse(dateStr, dateFormatter);
            return new SimpleResponse<>(parsedDate);
        } catch (Exception e) {
            log.error("String parse to LocalDate method has error: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.InvalidRequest.getCode(), e.getMessage());
        }
    }

    public static SimpleResponse<Date> strToDate(String date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return new SimpleResponse<>(formatter.parse(date));
        } catch (ParseException e) {
            return new SimpleResponse<>(ResultState.InvalidRequest.getCode(), e.getMessage());
        }
    }

    public static LocalDateTime dateToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.of("Asia/Tashkent"))
                .toLocalDateTime();
    }
}
