package uz.davrbank.repay.commonlogging.support;

public final class SuccessResolver {

    private SuccessResolver() {
    }

    public static boolean of(Integer status, String errorMessage) {
        return status != null
                && status >= 200
                && status < 400
                && (errorMessage == null || errorMessage.isBlank());
    }
}
