package uz.repay.card.service.web;

import uz.repay.card.model.dto.dm2.response.DM2CardsByPinflResponse;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

public interface DM2Service {

    ResultData<List<DM2CardsByPinflResponse>> getClientCardsByPINFL(String personCode);
}
