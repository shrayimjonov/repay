package uz.repay.payment.model.dto.core.transaction.response.result;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ExcelFile {
    private final String fileName;
    private final byte[] content;
}
