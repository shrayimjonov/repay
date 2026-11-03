package uz.repay.loan.model.dto.nciGateway.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Subject;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NCILoanInfoRepayResponse {

    @JsonProperty("id")
    private String clientId;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("birthday")
    private String birthday;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("patronymic")
    private String patronymic;

    @JsonProperty("passport_serial")
    private String passportSerial;

    @JsonProperty("passport_number")
    private String passportNumber;

    @JsonProperty("phone_mobile")
    private String phoneMobile;

    @JsonProperty("id_nibbd")
    private String idNibbd;

    @JsonProperty("code_gender")
    private String codeGender;

    @JsonProperty("birth_place")
    private String birthPlace;

    @JsonProperty("passport_date_registration")
    private String passportDateRegistration;

    @JsonProperty("passport_date_expiration")
    private String passportDateExpiration;

    @JsonProperty("code_subject")
    private Subject codeSubject;

    @JsonProperty("code_resident")
    private String codeResident;

    @JsonProperty("bpr_name")
    private String productName;

    @JsonProperty("person_code")
    private String personCode;

    @JsonProperty("inn")
    private String inn;
}
