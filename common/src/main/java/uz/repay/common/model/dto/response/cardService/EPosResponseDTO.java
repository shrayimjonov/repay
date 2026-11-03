package uz.repay.common.model.dto.response.cardService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EPosResponseDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("branch")
    private Branch branch;
    @JsonProperty("processing")
    private Processing processing;
    @JsonProperty("merchantId")
    private String merchantId;
    @JsonProperty("service")
    private ServiceType service;
    @JsonProperty("terminalId")
    private String terminalId;
    @JsonProperty("account")
    private String account;
}
