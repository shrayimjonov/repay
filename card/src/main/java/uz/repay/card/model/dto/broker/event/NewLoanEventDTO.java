package uz.repay.card.model.dto.broker.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewLoanEventDTO {
    @JsonProperty("after")
    NewLoanEventAfter after;
    NewLoanEventSource source;
    String op;
    Long ts_ms;
    Long ts_us;
    Long ts_ns;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewLoanEventAfter {
        @JsonProperty("id")
        Long id;

        @JsonProperty("external_id")
        String externalId;

        @JsonProperty("birth_place")
        String birthPlace;

        @JsonProperty("birthday")
        String birthday;

        @JsonProperty("branch")
        String branch;

        @JsonProperty("code_gender")
        String codeGender;

        @JsonProperty("id_nibbd")
        String idNIBBD;

        @JsonProperty("passport_date_registration")
        String passportDateRegistration;

        @JsonProperty("passport_date_expiration")
        String passportDateExpiration;

        @JsonProperty("passport_serial")
        String passportSerial;

        @JsonProperty("passport_number")
        String passportNumber;

        @JsonProperty("surname")
        String surname;

        @JsonProperty("first_name")
        String firstName;

        @JsonProperty("patronymic")
        String patronymic;

        @JsonProperty("pinfl")
        String pinfl;

        @JsonProperty("payer_phone")
        String payerPhone;

        @JsonProperty("resident")
        Boolean resident;

        @JsonProperty("subject")
        String subject;

        @JsonProperty("currency")
        String currency;

        @JsonProperty("created_time")
        Long createdTime;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewLoanEventSource {
        String version;
        String connector;
        String name;
        Long ts_ms;
        String snapshot;
        String db;
        String sequence;
        Long ts_us;
        Long ts_ns;
        String schema;
        String table;
        Long txId;
        Long lsn;
    }
}
