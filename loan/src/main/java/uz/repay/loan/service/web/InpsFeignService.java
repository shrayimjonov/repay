package uz.repay.loan.service.web;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.InpsInfo;

public interface InpsFeignService {
    ResultData<InpsInfo> getClientDataByPersonCode(String personCode);
}
