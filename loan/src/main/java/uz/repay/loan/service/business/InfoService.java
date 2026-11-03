package uz.repay.loan.service.business;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.info.response.BankDayResponseDTO;

import java.util.List;

public interface InfoService {
    ResultData<List<BankDayResponseDTO>> checkBankDay();
}
