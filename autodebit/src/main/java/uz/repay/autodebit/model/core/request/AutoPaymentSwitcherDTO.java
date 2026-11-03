package uz.repay.autodebit.model.core.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoPaymentSwitcherDTO {
    @NotNull
    public Boolean enable;
}
