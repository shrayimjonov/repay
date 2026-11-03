package uz.repay.payment.model.dto.nciBilling.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NCIDayResponse {
    @JsonProperty("00981")
    public boolean _00981;
    @JsonProperty("01046")
    public boolean _01046;
    @JsonProperty("01069")
    public boolean _01069;
    @JsonProperty("01072")
    public boolean _01072;
    @JsonProperty("01086")
    public boolean _01086;
    @JsonProperty("01121")
    public boolean _01121;
    @JsonProperty("01122")
    public boolean _01122;
}
