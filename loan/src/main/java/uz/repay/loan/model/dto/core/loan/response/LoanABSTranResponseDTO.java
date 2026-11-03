package uz.repay.loan.model.dto.core.loan.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.loan.model.dto.nciBilling.response.TransactionResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanABSTranResponseDTO {
    public String id;
    public String branch;
    public String debitAccount;
    public String debitName;
    public String creditAccount;
    public String creditName;
    public String purpose;
    public String amount;
    public String date;
    public String status;

    public LoanABSTranResponseDTO(TransactionResponse response) {
        this.id = response.getId();
        this.branch = response.getBranch();
        this.debitAccount = response.getClientAccount();
        this.debitName = response.getClientName();
        this.creditAccount = response.getCorrespondenceAccount();
        this.creditName = response.getCorrespondenceName();
        this.purpose = response.getPurpose();
        this.amount = response.getAmount();
        this.date = response.getDate();
        this.status = response.getStatus();
    }
}
