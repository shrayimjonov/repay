package uz.repay.card.service.business;

import uz.repay.card.model.dto.elma.response.ElmaVisaCardFindResponse;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.List;

public interface ElmaCardService {
    SimpleResponse<List<ElmaVisaCardFindResponse.ElmaVisaCardResult>> findClientVisaCardsByCodes(String personCode);
}
