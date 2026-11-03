package uz.repay.payment.model.dto.core.transaction.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionExcelRequestDTO {
    private TransactionFilterRequestDTO.TransactionParams params;
}
