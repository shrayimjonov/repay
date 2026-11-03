package uz.repay.card.model.dto.core.request.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uz.repay.common.model.dto.response.annotations.LoanId;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CardFinderRequestDTO extends CardBaseParam {

}
