package uz.repay.payment.model.dto.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;

@Getter
@Setter
@Component
@RequestScope
public class PayRequestScopedContext {

    private LoanAdditionalResponseDTO loanAdditionalResponseDTO;

}
