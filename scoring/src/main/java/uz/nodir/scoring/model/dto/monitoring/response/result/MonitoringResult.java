package uz.nodir.scoring.model.dto.monitoring.response.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.scoring.model.enums.monitoring.MonitoringOperation;
import uz.nodir.scoring.util.MonitoringDateDeSerializer;
import uz.nodir.scoring.util.MonitoringTimeDeSerializer;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoringResult {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("card")
    private String card;
    @JsonProperty("token")
    private String token;
    @JsonProperty("paymentId")
    private String paymentId;
    @JsonProperty("transType")
    private String transType;
    @JsonProperty("createdTime")
    @JsonFormat(pattern = "hh:mm:ss")
    @JsonDeserialize(using = MonitoringTimeDeSerializer.class)
    private LocalTime createdTime;
    @JsonProperty("createdDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = MonitoringDateDeSerializer.class)
    private LocalDate createdDate;
    @JsonProperty("merchant")
    private String merchant;
    @JsonProperty("terminal")
    private String terminal;
    @JsonProperty("creditOrDebit")
    private MonitoringOperation creditOrDebit;
    @JsonProperty("amount")
    private Long amount;
    @JsonProperty("amountWithCommission")
    private Long amountWithCommission;
    @JsonProperty("merchantName")
    private String merchantName;
    @JsonProperty("address")
    private String address;
    @JsonProperty("reversal")
    private boolean reversal;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("processingId")
    private String processingId;

    @Override
    public String toString() {
        return "MonitoringResult{" +
                "paymentId='" + paymentId + '\'' +
                '}';
    }

}
