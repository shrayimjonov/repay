package uz.repay.card.model.dto.nciBilling.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NCIClientInfoByPersonCode {
    @JsonProperty("id")
    private String id;

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("number_TAX_REGISTRATION")
    private String numberTaxRegistration;

    @JsonProperty("post_ADDRESS")
    private String postAddress;

    @JsonProperty("id_CLIENT")
    private String idClient;

    @JsonProperty("passport_SERIAL")
    private String passportSerial;

    @JsonProperty("passport_NUMBER")
    private String passportNumber;

    @JsonProperty("phone_HOME")
    private Object phoneHome;

    @JsonProperty("passport_DATE_REGISTRATION")
    private String passportDateRegistration;

    @JsonProperty("phone_MOBILE")
    private String phoneMobile;

    @JsonProperty("id_NIBBD")
    private String idNIBBD;

    @JsonProperty("birthday")
    private String birthday;

    @JsonProperty("code_RESIDENT")
    private String codeResident;

    @JsonProperty("code_COUNTRY")
    private String codeCountry;

    @JsonProperty("code_ADR_REGION")
    private String codeAddressRegion;

    @JsonProperty("code_ADR_DISTR")
    private String codeAddressDistrict;

    @JsonProperty("code_GENDER")
    private String codeGender;

    @JsonProperty("pinfl")
    private String personCode;

    @JsonProperty("family")
    private String surname;

    @JsonProperty("first_NAME")
    private String firstName;

    @JsonProperty("patronymic")
    private String patronymic;
}
