package uz.repay.common.model.dto.request.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientInfoRequestDTO {
    @JsonProperty("person_code")
    @NotBlank
    private String personCode;

    @JsonProperty("birthday")
    @NotNull
    private LocalDate birthday;
}
