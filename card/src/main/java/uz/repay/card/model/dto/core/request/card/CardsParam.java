package uz.repay.card.model.dto.core.request.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardsParam {
    private Processing processing;
    private List<CardInfoParam> params;
}
