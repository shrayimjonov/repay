package uz.repay.loan.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.info.response.BankDayResponseDTO;
import uz.repay.loan.service.business.InfoService;
import uz.repay.loan.service.web.NCIGatewayClientService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {
    private final NCIGatewayClientService nciGatewayClientService;

    @Override
    public ResultData<List<BankDayResponseDTO>> checkBankDay() {
        log.info("Received command to check bank day...");

        var dayResult = nciGatewayClientService.checkBankDay();

        if (!dayResult.getStatus()) {
            log.error("Day result can't fetch from NCI_GATEWAY");
            return new ResultData<>(dayResult.getError().getCode(), dayResult.getError().getMessage());
        }

        if (dayResult.getData().isEmpty()) {
            log.error("Data is empty");
            return new ResultData<>(4443, "Day response Data is empty...");
        }

        var result = ResultData.ok(dayResult
                .getData()
                .stream()
                .map(BankDayResponseDTO::new)
                .toList());


        log.info("Finished command to check bank day with response: {}", result);
        return result;
    }
}
