package uz.repay.payment.model.dto.core.transaction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import uz.repay.common.model.dto.response.core.result.PageResult;
import uz.repay.payment.model.dto.core.pay.response.PayResponseDTO;
import uz.repay.payment.model.entity.Transaction;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransactionFilterResponseDTO extends PageResult {
    private List<PayResponseDTO> list;

    public TransactionFilterResponseDTO(Page<Transaction> transactionsPage) {
        super(transactionsPage.getSize(),
                transactionsPage.getNumber() + 1,
                transactionsPage.getNumberOfElements(),
                transactionsPage.getTotalPages(),
                transactionsPage.getTotalElements(),
                transactionsPage.isLast(),
                transactionsPage.isFirst()
        );

        this.list = transactionsPage
                .getContent()
                .stream()
                .map(PayResponseDTO::new)
                .toList();
    }
}
