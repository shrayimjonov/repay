package uz.repay.loan.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.InpsInfo;
import uz.repay.loan.service.business.ClientIncomeService;
import uz.repay.loan.service.web.InpsFeignService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientIncomeServiceImpl implements ClientIncomeService {
    private final InpsFeignService inpsFeignService;

    public ResultData<InpsInfo> checkClientIncomeByPersonCode(@NotNull String personCode) {
        validatePersonCode(personCode);
        return inpsFeignService.getClientDataByPersonCode(personCode);
    }

    private void validatePersonCode(String personCode) {
        log.info("Validating person code: {}", personCode);

        if (personCode == null || personCode.isEmpty() || !personCode.matches("^\\d{14}$"))
            throw new InvalidDataException("Person code must be consist of 14 digits!");
    }
}
