package uz.repay.loan.model.dto.nciBilling.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.enums.Subject;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientInfoResponse {
    @JsonProperty("id")
    private String clientId;
    @JsonProperty("branch")
    private String branch;
    @JsonProperty("state")
    private String state;
    @JsonProperty("name")
    private String fullName;
    @JsonProperty("date_OPEN_NIBBD")
    private String dateOpenNibbd;
    @JsonProperty("date_CLOSE_NIBBD")
    private String dateCloseNibbd;
    @JsonProperty("number_TAX_REGISTRATION")
    private String inn;
    @JsonProperty("passport_SERIAL")
    private String passportSerial;
    @JsonProperty("passport_NUMBER")
    private String passportNumber;
    private String pinfl;
    @JsonProperty("phone_HOME")
    private String phoneHome;
    @JsonProperty("passport_DATE_REGISTRATION")
    private String passportDateRegistration;
    @JsonProperty("phone_MOBILE")
    private String phoneMobile;
    @JsonProperty("family")
    private String surname;
    @JsonProperty("first_NAME")
    private String name;
    @JsonProperty("patronymic")
    private String patronymic;
    @JsonProperty("id_NIBBD")
    private String idNibbd;
    @JsonProperty("post_ADDRESS")
    private String address;
    @JsonProperty("birthday")
    private String birthday;
    @JsonProperty("code_ADR_REGION")
    private String codeRegion;
    @JsonProperty("code_ADR_DISTR")
    private String codeDistrict;
    @JsonProperty("code_GENDER")
    private String codeGender;
    @JsonProperty("passport_PLACE_REGISTRATION")
    private String passportPlaceRegistration;
    @JsonProperty("code_BANK")
    private String codeBank;
    @JsonProperty("code_CITIZENSHIP")
    private String codeCitizenship;
    @JsonProperty("birth_PLACE")
    private String birthPlace;
    @JsonProperty("email_ADDRESS")
    private String email;
    @JsonProperty("type_DOCUMENT")
    private String documentType;
    @JsonProperty("passport_DATE_EXPIRATION")
    private String passportDateExpiration;
    @JsonProperty("code_RESIDENT")
    private String resident;
    @JsonProperty("code_SUBJECT")
    private Subject subject;
    @JsonProperty("error")
    private CoreError error;
}
