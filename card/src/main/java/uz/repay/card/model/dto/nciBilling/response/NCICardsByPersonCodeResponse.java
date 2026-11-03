package uz.repay.card.model.dto.nciBilling.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NCICardsByPersonCodeResponse {
    private String branch;
    private String client;
    private String pan;
    private String expiry;
    private String holder;
}
