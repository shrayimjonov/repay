package uz.repay.card.service.business.adapter;

import uz.repay.card.model.dto.humo.request.HumoCardInfoRequestDTO;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponseList;
import uz.repay.common.model.dto.response.core.ResultData;

/**
 * Powered by: Shukhratjon Rayimjonov
 * Date: 18.06.2026
 */
public interface CardInfoService {

    Object cardInfo(Long loanId);

    ResultData<HumoCardInfoResponseList> getHumoCardsInfoByLoanExtIds(HumoCardInfoRequestDTO requestDTO);
}
