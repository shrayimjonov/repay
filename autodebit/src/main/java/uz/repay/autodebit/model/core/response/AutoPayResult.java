package uz.repay.autodebit.model.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.autodebit.utils.SessionUtils;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoPayResult {
    private Map<String, ResultData<?>> successList = new HashMap<>();
    private Map<String, ResultData<?>> failedList = new HashMap<>();
    private String sessionID = SessionUtils.generateSessionID();
    private String branch;

    public AutoPayResult(String branch) {
        this.branch = branch;
    }
}
