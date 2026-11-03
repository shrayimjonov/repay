package uz.repay.autodebit.model.card.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uz.repay.common.model.dto.response.core.CoreError;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HumoCardInfoResponse(
        String id,
        HumoCardResult result,
        CoreError error
) {

    public HumoCardInfoResponse(HuMoCardBaseResponse<HumoCardResult> response) {
        this(
                response.getId(),
                response.getResult(),
                response.getError()
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HumoCardResult(
            Integer listSize,
            HumoCard card,
            ArrayList<HumoCardAccount> account,
            HumoCardBalance balance,
            Mb mb) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HumoCardBalance(
            Long currency,
            Long initialAmount,
            Long bonusAmount,
            Long creditLimit,
            Long lockedBackofficeAmount,
            Long lockedBackofficeAmountOffline,
            Long lockedAmount,
            Long availableAmount) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HumoCardAccount(
            String institutionId,
            String accountId,
            String bankAccountId,
            String currency,
            String cardholderId,
            String effectiveDate,
            String updateDate,
            String accountType,
            String lockedBackofficeAmount,
            String lockTime,
            String shadowAmount,
            String commissionGroup,
            String priority,
            String status,
            String availableAmount) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HumoCard(
            String institutionId,
            String primaryAccountNumber,
            String effectiveDate,
            String updateDate,
            Integer prefixNumber,
            String expiry,
            String cardSequenceNumber,
            String cardholderId,
            String nameOnCard,
            String accountRestrictionsFlag,
            String commissionGroup,
            String cardUserId,
            String additionalInfo,
            String riskGroup,
            String riskGroup2,
            String bankC,
            String pinTryCount,
            Statuses statuses) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Statuses(ArrayList<Item> item) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Mb(
            String state,
            String phone,
            String message) {

    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String type,
            String actionCode,
            String actionDescription,
            String effectiveDate) {

    }
}
