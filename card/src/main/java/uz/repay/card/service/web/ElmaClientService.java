package uz.repay.card.service.web;

import uz.repay.card.model.dto.elma.request.ElmaVisaCardFindRequest;
import uz.repay.card.model.dto.elma.response.ElmaVisaCardFindResponse;
import uz.repay.common.model.dto.response.core.SimpleResponse;

public interface ElmaClientService {

    SimpleResponse<ElmaVisaCardFindResponse> getClientCardsByPINFL(ElmaVisaCardFindRequest request);
}
