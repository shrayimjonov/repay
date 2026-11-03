package uz.repay.card.model.web;

import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.ResultState;


@Slf4j
public class LoanInfoResponseFakeData {

    public static ResultData<LoanInfoResponse> buildSuccessResponse() {
        LoanInfoResponse loanInfoResponse = new LoanInfoResponse();
        loanInfoResponse.setMfo(Branch._01121.getCode());
        loanInfoResponse.setClientName("SIROJIDDINOV NODIRBEK SHAXOBJONOVICH");
        loanInfoResponse.setBirthday("1996-08-29");
        loanInfoResponse.setMainAccountBalance(0L);
        loanInfoResponse.setPrincipalBalance(39859658L);
        loanInfoResponse.setArrearsOnPrincipal(39859658L);
        loanInfoResponse.setOfOverdueDebt(0L);
        loanInfoResponse.setInterestOnTermDebt(1255859L);
        loanInfoResponse.setOfOverdueInterest(0L);
        loanInfoResponse.setArrearsOfInterest(0L);
        loanInfoResponse.setRecommendedAmount(0L);
        loanInfoResponse.setMaxAmount(41115517L);
        loanInfoResponse.setTransitBalance(4484153L);
        loanInfoResponse.setOverdueCovenantCommission(0L);
        loanInfoResponse.setLoanAmount(300000000L);
        loanInfoResponse.setLoanClose("2025-10-01");
        loanInfoResponse.setLoanOpen("2024-10-02");
        loanInfoResponse.setLoanType("Микрозаем");
        loanInfoResponse.setLoss_reserve("0");
        loanInfoResponse.setCurrency("000");
        loanInfoResponse.setCourtCosts("0");
        loanInfoResponse.setTransitAccount("29801000660272385600");
        var result = ResultData.ok(loanInfoResponse);

        log.info("Build fake success data: {}", result);
        return result;
    }

    public static ResultData<LoanInfoResponse> buildErrorResponse() {
        var message = "158076 loan in 01121 is closed";
        log.info("Build fake failed data: {}", message);
        return new ResultData<>(
                ResultState.InternalServerError.getCode(),
                message
        );
    }


}
