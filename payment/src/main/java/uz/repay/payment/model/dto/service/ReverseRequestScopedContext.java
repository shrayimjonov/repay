package uz.repay.payment.model.dto.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import uz.repay.payment.model.entity.Transaction;

@Getter
@Setter
@Component
@RequestScope
public class ReverseRequestScopedContext {

    private Transaction transaction;

}
