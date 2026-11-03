package uz.repay.loan.model.dto.core.info.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.loan.model.dto.nciGateway.response.NCIBankDayResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankDayResponseDTO {
    private String branch;
    private String name;
    private String currentDate;
    private String status;
    private String process;

    public BankDayResponseDTO(NCIBankDayResponse nciBankDayResponse) {
        this.branch = nciBankDayResponse.getBank_id();
        this.name = nciBankDayResponse.getShort_name();
        this.currentDate = nciBankDayResponse.getCurr_date().toString();

        if (!nciBankDayResponse.getStatus_open().equals("-"))
            this.status = nciBankDayResponse.getStatus_open();

        if (!nciBankDayResponse.getStatus_close().equals("-"))
            this.status = nciBankDayResponse.getStatus_close();

        this.process = nciBankDayResponse.getProcess();
    }
}
