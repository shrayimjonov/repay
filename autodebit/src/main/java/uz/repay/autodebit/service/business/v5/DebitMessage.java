package uz.repay.autodebit.service.business.v5;
public record DebitMessage(
        String loanExtId,
        String branchId,
        String cycleId,
        long deadlineEpochMillis
) {
    public boolean isExpired(long nowEpochMillis) {
        return deadlineEpochMillis > 0 && nowEpochMillis > deadlineEpochMillis;
    }

    public boolean isValid() {
        return loanExtId != null && !loanExtId.isBlank()
                && branchId != null && !branchId.isBlank()
                && cycleId != null && !cycleId.isBlank();
    }
}