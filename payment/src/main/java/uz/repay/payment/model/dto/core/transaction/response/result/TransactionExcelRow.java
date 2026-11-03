package uz.repay.payment.model.dto.core.transaction.response.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.ResponseStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class TransactionExcelRow {
    private final String loanExtId;
    private final String cardOwner;
    private final String cardPan;
    private final Long amount;
    private final Currency currency;
    private final ResponseStatus status;
    private final AddType payType;
    private final String refNum;
    private final String terminalId;
    private final LocalDateTime initTime;
    private final String createdBy;
    private final String note;
    private final String reverseReason;

    private final LocalDate createdDate;
    private final Long id;
}
