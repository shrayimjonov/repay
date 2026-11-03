package uz.repay.common.model.dto.request.card.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardFullInfoRequestDTO {
    @NotBlank
    private String pan;
}
