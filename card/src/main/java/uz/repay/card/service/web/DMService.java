package uz.repay.card.service.web;

import uz.repay.card.model.dto.dm.response.CardsByPINFLResponse;
import uz.repay.common.model.dto.response.core.ResultData;

public interface DMService {

    ResultData<CardsByPINFLResponse> getClientCardsByPINFL(String personCode);
}
