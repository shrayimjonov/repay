package uz.repay.payment.service.business.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.BranchUtils;
import uz.repay.payment.service.web.NCIBillingService;

import static uz.repay.payment.utils.TranUtils.dayResult;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankDayChecker {
    private final NCIBillingService nciBillingService;

    public boolean checkBankDay(Processing pc, String loanExtId) {
        if (pc.equals(Processing.DAVR_VISA)) {
            var bankDayResponse = nciBillingService.getBankDay();

            return dayResult(bankDayResponse, BranchUtils.getBranchByLoan(loanExtId).getCode());

        } else {
            log.info("Bank day not necessary checking for: {}", pc);
            return true;
        }
    }

    public boolean checkBankDayForAll(String loanExtId) {

        var bankDayResponse = nciBillingService.getBankDay();

        return dayResult(bankDayResponse, BranchUtils.getBranchByLoan(loanExtId).getCode());


    }
}
