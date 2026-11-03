package uz.repay.loan.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.annotations.LoanId;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;
import uz.repay.loan.model.dto.core.loan.response.LoanClientInfoResponseDTO;
import uz.repay.loan.service.business.LoanInfoService;
import uz.repay.loan.service.core.LoanDAO;


@Service
@Slf4j
@RequiredArgsConstructor
public class LoanInfoServiceImpl implements LoanInfoService {
    private final LoanDAO dao;

    @Override
    public ResultData<LoanClientInfoResponseDTO> getLoanClientInfoByExtId(@LoanId String loanExtId) {
        log.info("Received command to get loan client info by extId: {}", loanExtId);

        var loanOptional = dao.findByExtIdV2(loanExtId);

        if (loanOptional.isEmpty()) {
            var message = "Loan with extId: %s not found".formatted(loanExtId);
            log.error("Finished command. Cause: {}", message);
            return new ResultData<>(ResultState.WrongParams, message);
        }

        var response = ResultData.ok(
                new LoanClientInfoResponseDTO(
                        loanOptional.get()
                )
        );

        log.info("Finished command to get loan client info with response dto: {}", response);
        return response;
    }


}
