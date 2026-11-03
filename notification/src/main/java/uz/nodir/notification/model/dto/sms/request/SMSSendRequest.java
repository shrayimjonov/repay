package uz.nodir.notification.model.dto.sms.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.nodir.notification.model.enums.Provider;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SMSSendRequest {
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("text")
    private String text;
    @JsonProperty("provider")
    private Provider provider;
}
