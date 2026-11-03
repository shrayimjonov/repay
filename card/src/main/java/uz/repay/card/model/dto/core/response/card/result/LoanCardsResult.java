package uz.repay.card.model.dto.core.response.card.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.master.response.MasterBalanceResponse;
import uz.repay.card.model.dto.uzcard.response.result.BalanceResult;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.base.BaseCardEntity;
import uz.repay.card.util.ServiceUtils;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.core.params.DebitCardParams;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.CardUtils;

import static uz.repay.card.util.ServiceUtils.improvePhone;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanCardsResult {
    @JsonProperty("id")
    @Schema(name = "id", example = "864", description = "card id")
    private Long id;

    @JsonProperty("pan")
    @Schema(name = "pan", example = "561468******7308", description = "masked pan")
    private String pan;

    @JsonProperty("holder")
    @Schema(name = "holder", example = "NODIRBEK SIROJIDDINOV", description = "card holder")
    private String holder;

    @JsonProperty("phone")
    @Schema(name = "phone", example = "998901234567", description = "phone attached to card")
    private String phone;

    @JsonProperty("sms")
    @Schema(name = "sms", example = "false", description = "sms information is available")
    private Boolean sms;

    @JsonProperty("amount")
    @Schema(name = "amount", example = "364100", description = "card balance")
    private Long amount;

    @JsonProperty("state")
    @Schema(name = "state", example = "0", description = "card status")
    private Integer state;

    @JsonProperty("stateDefinition")
    @Schema(name = "stateDefinition", example = "FAOL", description = "card status translated description")
    private String stateDefinition;

    @JsonProperty("payer")
    @Schema(name = "payer", example = "GUARANTOR", description = "what is the relationship of the card holder to the creditor")
    private Payer payer = Payer.AUTO;

    @JsonProperty("processing")
    @Schema(name = "processing", example = "DAVR_VISA", description = "card processing")
    private Processing processing;

    @JsonProperty("debitParams")
    @Schema(name = "debitParams", example = "{\n" +
            "                \"type\": \"pan\",\n" +
            "                \"card\": \"ENCRYPTED_CARD_TOKEN\",\n" +
            "                \"processing\": \"HUMO\"\n" +
            "            }", description = "debit params for sending to payment service")
    private DebitCardParams debitParams;

    @JsonProperty("ipAddress")
    @Schema(name = "ipAddress", example = "127.0.0.1", description = "card adder employee ip address")
    private String ipAddress;

    @JsonProperty("currency")
    @Schema(name = "currency", example = "UZS", description = "card currency")
    private Currency currency;

    @JsonProperty("guarantorId")
    @Schema(name = "guarantorId", example = "59", description = "guarantor id if card taken from guarantor")
    private Long guarantorId;

    @JsonProperty("is_confirmed")
    @Schema(name = "is_confirmed", example = "true", description = "card is confirmed by holder")
    private Boolean isConfirmed = Boolean.TRUE;

    public LoanCardsResult(Long id, SimpleResponse<HumoCardInfoResponse> result) {
        this.id = id;
        this.holder = result.getData().result().card().nameOnCard();

        if (result.getData().result().mb() != null && result.getData().result().mb().phone() != null) {
            this.phone = improvePhone(result.getData().result().mb().phone());
            this.sms = result.getData().result().mb().state().equals("on");
        } else
            this.sms = false;


        this.pan = CardUtils.maskPanDigits(result.getData().result().card().primaryAccountNumber());
        this.amount = result.getData().result().balance().availableAmount();

        var cardStatus = result.getData().result().card().statuses()
                .item()
                .stream()
                .filter(c -> c.type().equals("card"))
                .findFirst();
        this.state = cardStatus.map(item -> Integer.valueOf(item.actionCode())).orElse(1101);
        if (result.getData().result().card().pinTryCount().equals("3"))
            this.state = 1106;
        this.processing = Processing.HUMO;
        if (state == 0)
            this.debitParams = new DebitCardParams(result.getData().result().card().primaryAccountNumber(), Processing.HUMO);
        this.currency = Currency.UZS;
    }

    public LoanCardsResult(BaseCardEntity s, Boolean processingIsEnabled) {
        var u = s.getCard();
        this.payer = s.getPayer();
        this.id = u.getId();
        this.holder = u.getHolder();
        this.phone = u.getPhone();
        this.sms = false;
        this.pan = u.getMaskedPan();
        this.amount = 0L;
        if (s.getIsDeleted())
            this.state = 1105;
        else if (ServiceUtils.isExpiredCard(u.getExpiry()))
            this.state = 1104;
        else if (!s.getCard().getIsActive())
            this.state = 1103;
        else if (!processingIsEnabled)
            this.state = 1101;
        else if (!s.getIsConfirmed()) {
            this.isConfirmed = false;
            this.state = 1108;
        } else
            this.state = 1102;
        this.processing = u.getProcessing();
        this.currency = u.getCurrency();
        this.ipAddress = s.getIpAddress();
    }


    public LoanCardsResult(BaseCardEntity s) {
        var u = s.getCard();
        this.payer = s.getPayer();
        this.id = u.getId();
        this.holder = u.getHolder();
        this.phone = u.getPhone();
        this.sms = false;
        this.pan = u.getMaskedPan();
        this.amount = 0L;
        if (ServiceUtils.isExpiredCard(u.getExpiry()))
            this.state = 1104;
        else
            this.state = 1103;
        this.processing = u.getProcessing();
        this.currency = u.getCurrency();
    }

    public static LoanCardsResult balanceUnavailable(BaseCardEntity s) {
        var result = new LoanCardsResult();
        var u = s.getCard();
        result.payer = s.getPayer();
        result.id = u.getId();
        result.holder = u.getHolder();
        result.phone = u.getPhone();
        result.sms = false;
        result.pan = u.getMaskedPan();
        result.amount = null;
        result.state = ServiceUtils.isExpiredCard(u.getExpiry()) ? 1104 : 1101;
        result.processing = u.getProcessing();
        result.currency = u.getCurrency();
        result.ipAddress = s.getIpAddress();
        result.isConfirmed = s.getIsConfirmed();
        return result;
    }

    public LoanCardsResult(Long id, CardBaseResult u, Processing processing) {
        this.id = id;
        this.holder = u.getFullName();
        this.phone = u.getPhone();
        this.sms = u.getSms();
        this.pan = u.getPan().matches("\\d{16}") ? CardUtils.maskPanDigits(u.getPan()) : u.getPan();
        this.amount = u.getBalance();
        this.state = u.getPinCount() == 3 ? 1106 : u.getStatus(); // pinCode block
        this.processing = processing;
        if (u.getSms() && u.getStatus().equals(0) && !u.getPinCount().equals(3))
            this.debitParams = new DebitCardParams(u.getId(), processing);
        this.currency = CardUtils.isSumPC(processing) ? Currency.UZS : Currency.USD;
    }

    public LoanCardsResult(Long k, BalanceResult.BalanceInfo u, Card card, Processing processing) {
        this.id = k;
        this.holder = card.getHolder();
        this.phone = "998901234567";
        this.sms = true;
        this.pan = u.getPan().matches("\\d{16}") ? CardUtils.maskPanDigits(u.getPan()) : u.getPan();
        this.amount = u.getBalance();
        this.state = 0; // pinCode block
        this.processing = processing;
        this.debitParams = new DebitCardParams(u.getId(), processing);
        this.currency = CardUtils.isSumPC(processing) ? Currency.UZS : Currency.USD;
    }


    public LoanCardsResult(Long k, MasterBalanceResponse u) {
        this.id = k;
        this.holder = u.getHolder();
        this.phone = u.getPhone();
        this.sms = true;
        this.pan = u.getPan().matches("\\d{16}") ? CardUtils.maskPanDigits(u.getPan()) : u.getPan();
        this.amount = u.getBalance();
        this.state = u.getPinCount() == 3 ? 1106 : u.getStatus(); // pinCode block
        this.processing = Processing.DAVR_MASTER_CARD;
        if (u.getStatus() == 0)
            this.debitParams = new DebitCardParams(u.getToken(), processing);
        this.currency = CardUtils.isSumPC(processing) ? Currency.UZS : Currency.USD;
    }
}
