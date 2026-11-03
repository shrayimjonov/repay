package uz.repay.payment.model.dto.uzcard.request;

import jakarta.persistence.Access;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TransPayDto {
    private Long amount;
    private String cardId;
    private String ext;
    private String merchant;
    private String terminalId;
    private String stan;
    private int port;
}
