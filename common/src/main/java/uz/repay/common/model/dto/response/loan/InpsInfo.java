package uz.repay.common.model.dto.response.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InpsInfo {
    @JsonProperty("pass_sn")
    private String passportSerial;
    @JsonProperty("pass_num")
    private String passportNumber;
    @JsonProperty("name")
    private String name;
    @JsonProperty("resident")
    private String resident;
    @JsonProperty("birth_date")
    private String birthDate;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("adress")
    private String address;
    @JsonProperty("inps")
    private String personCode;
    @JsonProperty("invoices")
    private ArrayList<InpsResult> invoices;
    @JsonProperty("result_code")
    private Integer resultCode;
    @JsonProperty("result_message")
    private String resultMessage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InpsResult {
        @JsonProperty("inn")
        private String inn;
        @JsonProperty("org_name")
        private String organizationName;
        @JsonProperty("org_addres")
        private String organizationAddress;
        @JsonProperty("period")
        private String period;
        @JsonProperty("send_date")
        private String sendDate;
        @JsonProperty("total_invoices")
        private Double totalInvoices;
    }

}
