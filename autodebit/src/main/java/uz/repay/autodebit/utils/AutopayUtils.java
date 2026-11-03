package uz.repay.autodebit.utils;

import uz.repay.common.model.enums.Processing;

import java.util.List;

public final class AutopayUtils {

    public final static List<Processing> supportedProcessingTypesForMinimumReminder = List.of(Processing.HUMO, Processing.SVGATE_AUTOPAY);

    public final static Long minimumDebitAmountForMinimumReminder = 5000_00L;//1_236_000_00L;


}
