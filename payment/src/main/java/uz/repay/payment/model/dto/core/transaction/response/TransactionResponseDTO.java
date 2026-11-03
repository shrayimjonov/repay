package uz.repay.payment.model.dto.core.transaction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;
import uz.repay.payment.model.dto.core.pay.response.PayResponseDTO;
import uz.repay.payment.model.dto.core.transaction.response.result.BillResult;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.utils.PaymentUtils;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransactionResponseDTO extends PayResponseDTO {
    private List<BillResult> billResults;
    private Processing processing;

    public TransactionResponseDTO(Transaction transaction) {
        this.setId(transaction.getId());
        this.setBranch(transaction.getBranch());
        this.setCardOwner(transaction.getCardOwner());
        this.setStatus(transaction.getStatus());
        this.setRefNum(transaction.getRefNum());
        this.setNote(transaction.getNote());
        this.setAmount(transaction.getAmount());
        this.setReverseReason(transaction.getReverseReason());
        this.setLoanExtId(transaction.getLoanExtId());
        this.setCardPan(transaction.getCardPan());
        this.setPayType(transaction.getPayType());
        this.setInitTime(transaction.getInitTime());
        this.setCreatedBy(transaction.getCreatedBy());
        this.setTerminalId(transaction.getTerminalId());
        this.setCurrency(transaction.getCurrency());
        this.setAmounts(PaymentUtils.buildTransactionAmount(transaction));

        this.billResults = transaction
                .getBills()
                .stream()
                .map(BillResult::new)
                .toList();
        this.processing = transaction.getProcessing();
    }
}
