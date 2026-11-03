package uz.repay.loan.service.core.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.repay.loan.service.core.LoanDAO;
import uz.repay.loan.service.web.NCIGatewayClientService;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteLoanCheckerComponent {
    private final LoanDAO loanDAO;
    private final NCIGatewayClientService nciGatewayClientService;

    public synchronized void checkLoanToDeleted() {
        log.info("Starting check all loans status in ABS...");

        var loanList = loanDAO.findAllNotDeleted();


        for (var loan : loanList) {
            var absResponse = nciGatewayClientService.getLoanInfoByLoanId(loan.getExtId());

            if (!absResponse.getStatus()) {
                log.info("Loan: {} can't fetch, cause: {}", loan.getExtId(), absResponse.getError().getMessage());
                return;
            }

            //801 -> not found
            //803 -> closed loan
            if (absResponse.getData().getError() != null) {
                if (absResponse.getData().getError().getCode().equals(801)
                        || absResponse.getData().getError().getCode().equals(803)) {
                    log.info("ABS response is: {} and Loan will be deleted!", absResponse.getData().getError().getMessage());

                    loan.setIsDeleted(true);
                    loanDAO.save(loan);
                }
            }
        }


    }
}
