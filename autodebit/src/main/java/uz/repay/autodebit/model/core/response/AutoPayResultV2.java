package uz.repay.autodebit.model.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.autodebit.utils.SessionUtils;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoPayResultV2 {
    public Map<String, ResultData<?>> successList = new HashMap<>();
    public Map<String, ResultData<?>> failedList = new HashMap<>();
    /**
     * Loans whose payment call ended with an UNKNOWN outcome (timeout / server
     * error): money may have moved. Drives FAILED_UNKNOWN classification and the
     * re-debit cooldown - required for double-debit safety.
     */
    public Map<String, String> unknownOutcomeList = new HashMap<>();
    public String sessionID = SessionUtils.generateSessionID();
    public String branch;
    public Boolean emergencyExit = false;

    public AutoPayResultV2(String branch) {
        this.branch = branch;
    }
}
