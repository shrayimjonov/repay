package uz.repay.card.model.dto.core.request.switcher;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwitcherPatchDTO {
    @NotNull
    private Boolean isEnabled;
}
