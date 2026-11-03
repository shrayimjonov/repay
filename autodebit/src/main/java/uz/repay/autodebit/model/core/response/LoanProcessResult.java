package uz.repay.autodebit.model.core.response;

public enum LoanProcessResult {
    SUCCESS,
    FAILED,
    FAILED_UNKNOWN,
    SKIPPED_INACTIVE,
    SKIPPED_LOCKED;

    public boolean isSuccess() {
        return this == SUCCESS;
    }
}
