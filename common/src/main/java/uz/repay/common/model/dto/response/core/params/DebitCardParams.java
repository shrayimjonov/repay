package uz.repay.common.model.dto.response.core.params;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.exception.CardException;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.CardUtils;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class DebitCardParams {
    @NotNull
    private CardType type;
    @NotBlank
    private String card;
    @NotNull
    private Processing processing;
    private Long cardId;

    public DebitCardParams(String s, Processing processing) {
        this.processing = processing;

        switch (processing) {
            case HUMO, DAVR_VISA: {
                this.type = CardType.pan;
                this.card = CardUtils.encryptPan(s);
                break;
            }
            case UZCARD, SVGATE_AUTOPAY, DAVR_MASTER_CARD: {
                this.type = CardType.token;
                this.card = s;
                break;
            }
            default:
                throw new CardException("unexpected type of processing: %s".formatted(processing));

        }
    }
}
