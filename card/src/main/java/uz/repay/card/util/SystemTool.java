package uz.repay.card.util;

public class SystemTool {

    public static String HUMO_GATE_SUB_SYSTEM = "PAYMO";
    public static String HUMO_LOGIN = "";

    public static String requestIdGenerator() {
        return "DVR_REPAY_" + System.currentTimeMillis();
    }
}
