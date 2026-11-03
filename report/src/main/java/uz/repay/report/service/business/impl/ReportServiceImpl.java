package uz.repay.report.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.repay.common.exception.BadRequestException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.request.loan.params.LoanSearchParams;
import uz.repay.common.model.dto.response.loan.LoanGraphResponse;
import uz.repay.common.utils.DateUtils;
import uz.repay.common.utils.HttpServletUtils;
import uz.repay.report.model.dto.report.request.ReportRequestDTO;
import uz.repay.report.model.dto.debtLoan.response.DebtLoanResponse;
import uz.repay.report.model.dto.loan.response.LoanSimpleResponse;
import uz.repay.report.service.business.ReportService;
import uz.repay.report.service.component.ExcelReporter;
import uz.repay.report.service.web.LoanClientService;
import uz.repay.report.service.web.NCIGatewayClientService;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final LoanClientService loanClientService;
    private final NCIGatewayClientService nciGatewayClientService;

    @Override
    public void generateExcelReport(@NonNull ReportRequestDTO requestDTO) {
        validateDateFields(requestDTO);

        switch (requestDTO.getType()) {
            case LOAN -> allLoansReport(requestDTO.getParams());
            case DEBT_LOAN -> allDebtLoansReport();
            case LOAN_GRAPH -> loanGraph(requestDTO.getParams().getExtId());
            default ->
                    throw new InvalidDataException("For %s type not found excel generator".formatted(requestDTO.getType()));
        }
    }

    private static void validateDateFields(ReportRequestDTO requestDTO) {
        if (requestDTO.getParams() != null && requestDTO.getParams().getRangeParams() != null
                && requestDTO.getParams().getRangeParams().getStartDate() != null && !requestDTO.getParams().getRangeParams().getStartDate().isEmpty()
                && requestDTO.getParams().getRangeParams().getEndDate() != null && !requestDTO.getParams().getRangeParams().getEndDate().isEmpty())
            DateUtils.validateDateFields(requestDTO.getParams().getRangeParams().getStartDate(), requestDTO.getParams().getRangeParams().getEndDate());
    }

    private void allLoansReport(LoanSearchParams params) {
        var reporter = new ExcelReporter<LoanSimpleResponse>();
        var loanList = getLoanList(params);
        var map = Map.of("loan", loanList);
        var columns = List.of("extId", "payerName", "payerPhone", "idNibbd", "pinfl", "passport", "isConvicted", "createdBy", "isInsolvent");
        reporter
                .generate(map, columns, HttpServletUtils.getCurrentHttpResponse());
    }

    private List<LoanSimpleResponse> getLoanList(LoanSearchParams params) {
       try {
           return loanClientService.getAllLoans(params).getResult();
       } catch (Exception e) {
           throw new BadRequestException(e.getMessage());
       }
    }

    private void allDebtLoansReport() {
        var reporter = new ExcelReporter<DebtLoanResponse>();
        var loanList = getDebtLoanList();
        var map = Map.of("debtLoan", loanList);
        var columns = List.of("loanExtId", "payerName", "payerPhone", "recommended", "total", "pinfl", "passport", "createdDate", "createdBy");
        reporter
                .generate(map, columns, HttpServletUtils.getCurrentHttpResponse());
    }

    private List<DebtLoanResponse> getDebtLoanList() {
      try {
          return loanClientService.getAllDebtLoans().getResult();
      } catch (Exception e) {
          throw new BadRequestException(e.getMessage());
      }
    }

    private void loanGraph(String loanExtId) {
        if (loanExtId == null || loanExtId.isEmpty())
            throw new InvalidDataException("Loan ext id must be filled!");

        var reporter = new ExcelReporter<LoanGraphResponse>();


        var loanGraphResult = nciGatewayClientService.getLoanGraph(loanExtId);

        if (!loanGraphResult.getStatus())
            throw new BadRequestException(loanGraphResult.getError().getMessage());

        var loanGraphResponse = loanGraphResult.getData();

        if (loanGraphResponse.getError() != null)
            throw new BadRequestException(loanGraphResponse.getError().getMessage());

        var newData = loanGraphResponse.getResult()
                .stream()
                .map(z -> {
                    String operationName = z.getOperationId();

                    operationName = switch (operationName) {
                        case "1" -> "Kredit uchun ajratilgan mablag'".toUpperCase();
                        case "2" -> "Asosiy qarzdorlik uchun to'lov".toUpperCase();
                        case "4" -> "Foiz qarzdorlik uchun to'lov".toUpperCase();
                        default -> z.getOperationId();
                    };

                    return new LoanGraphResponse(z.getId(), operationName, z.getExpId(), z.getVDate(), z.getAmount(), z.getStatus(), z.getNum(), z.getRepay());
                })
                .toList();

        var map = Map.of("graph", newData);
        var columns = List.of("operationId", "vDate", "amount");


        reporter
                .generate(map, columns, HttpServletUtils.getCurrentHttpResponse());
    }


}