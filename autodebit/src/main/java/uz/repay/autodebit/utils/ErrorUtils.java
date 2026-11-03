package uz.repay.autodebit.utils;

import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.enums.Processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ErrorUtils {
    private final static Map<Processing, List<Integer>> processingCodes = new HashMap<>();


    static {
        var huMoErrors = new ArrayList<Integer>();
        huMoErrors.add(427); // card is expired
        huMoErrors.add(109); // installment card

        var uzCardErrors = new ArrayList<Integer>();
        uzCardErrors.add(-201); // card is expired


        processingCodes.put(Processing.HUMO, huMoErrors);
        processingCodes.put(Processing.UZCARD, uzCardErrors);
        processingCodes.put(Processing.SVGATE_AUTOPAY, uzCardErrors);


    }

    public static Boolean statusIsCardBlocked(Processing pc, Integer code) {
        log.info("The codes could be: {}", processingCodes);

        log.info("Check card status: {} from map for: {}", code, pc);

        var pcCodes = processingCodes.get(pc);

        if (pcCodes == null) {
            log.info("Not detected error codes for: {}", pc);
            return false;
        }


        return pcCodes
                .stream()
                .anyMatch(z -> z.equals(code));

    }
}
