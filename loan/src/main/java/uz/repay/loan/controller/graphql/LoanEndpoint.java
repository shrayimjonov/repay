package uz.repay.loan.controller.graphql;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.loan.model.dto.core.loan.request.LoanSearchRequestDTO;
import uz.repay.loan.model.dto.core.loan.response.LoanSearchResponseDTO;
import uz.repay.loan.service.business.LoanService;


@Controller
@RequiredArgsConstructor
public class LoanEndpoint {
    private final LoanService loanService;

    @QueryMapping
    public ResultData<LoanSearchResponseDTO> filter(@Argument @Valid LoanSearchRequestDTO requestDTO) {
        return loanService.findLoansByFilter(requestDTO);
    }

}
