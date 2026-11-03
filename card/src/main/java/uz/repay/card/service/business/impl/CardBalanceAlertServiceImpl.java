package uz.repay.card.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.autopay.request.AutoPayPushRequestDTO;
import uz.repay.card.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.P2PInfoResult;
import uz.repay.card.model.entity.LoanCard;
import uz.repay.card.service.business.CardBalanceAlertService;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.card.service.core.SwitcherRedisService;
import uz.repay.card.service.core.component.broker.KafkaCardAlertProducer;
import uz.repay.card.service.web.NCIBillingService;
import uz.repay.card.service.web.PaymentClientService;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.model.dto.request.kafka.CardCreditEventDTO;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.request.payment.params.NotificationParams;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.core.params.DebitCardParams;
import uz.repay.common.model.dto.response.payment.PaymentForcePayResponseDTO;
import uz.repay.common.model.enums.*;
import uz.repay.common.utils.CardCommon;
import uz.repay.common.utils.CardUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardBalanceAlertServiceImpl implements CardBalanceAlertService {
    private static final Integer HUMO_INSUFFICIENT_FUNDS_ERROR_CODE = 116;
    private static final Integer UZCARD_INSUFFICIENT_FUNDS_ERROR_CODE = 51;

    private final LoanCardDAO loanCardDAO;
    private final PaymentClientService paymentClientService;
    private final SwitcherRedisService switcherRedisService;
    private final NCIBillingService nciBillingService;
    private final UzCardService uzCardService;
    private final RedissonClient redissonClient;
    private final static Integer LOCK_WAIT_TIME = 1;
    private final static Integer LOCK_TTL = 300000;
    private final KafkaCardAlertProducer producer;


    @Async
    @Override
    public void pushToLoansWithAlertedCard(AutoPayPushRequestDTO requestDTO) {
        producer.sendToKafka(requestDTO);
    }

    @Override
    public void pushToLoansWithAlertedCard(AutoPayPushRequestDTO requestDTO, LocalDateTime alertedDateTime) {
        log.info("Begin checking loans which attached autoPay card: {}", requestDTO.getCardId());

        String pan = requestDTO.getPan();

        RLock lock = redissonClient.getLock("card-alert-to-debit by pan:" + pan);
        log.info("Try to lock card: {}", pan);

        boolean isLocked = false;
        try {
            // Пытаемся захватить блокировку на 5 секунд, с автоматическим освобождением через 30 сек
            isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_TTL, TimeUnit.MILLISECONDS);

            if (!isLocked) {
                log.error("Card: {} already handling!", pan);
                return;
            }

            autoPayHandler(requestDTO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Occurred error while blocking with message: {}", e.getMessage());
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock(); // Освобождаем лок
            }
            log.info("PROCESSED ALERT CARD: {}, {}", requestDTO.getCardId(), requestDTO.getPan());
            long duration = Duration.between(alertedDateTime, LocalDateTime.now()).toSeconds();
            if (duration < 5) {
                log.info("AUTOPAYMENT_WITH_ALERT_ENDED_LESS_THAN_5 {}", requestDTO.getCardId());
            } else if (duration < 10) {
                log.info("AUTOPAYMENT_WITH_ALERT_ENDED_LESS_THAN_10 {}", requestDTO.getCardId());

            } else if (duration < 30) {
                log.info("AUTOPAYMENT_WITH_ALERT_ENDED_LESS_THAN_30 {}", requestDTO.getCardId());
            } else if (duration < 60) {
                log.info("AUTOPAYMENT_WITH_ALERT_ENDED_LESS_THAN_60 {}", requestDTO.getCardId());
            } else {
                log.info("AUTOPAYMENT_WITH_ALERT_ENDED_MORE_THAN_60 {}", requestDTO.getCardId());
            }
        }
    }

    private void autoPayHandler(AutoPayPushRequestDTO requestDTO) {
        var switcher = switcherRedisService.findByName(Processing.SVGATE_AUTOPAY.name());

        if (switcher == null || !switcher.getIsEnabled()) {
            log.warn("Switcher for processing: {} is disabled and will be exited!", Processing.SVGATE_AUTOPAY);
            return;
        }

        debitFromCard(requestDTO.getCardId(), requestDTO.getBalance(), AddType.ALERTED, CardType.token);
    }

    private SimpleResponse<ResultData<PaymentForcePayResponseDTO>> sendRequestToPaymentService(Long amount, LoanCard loanCard,
                                                                                               AddType addType, CardType cardType) {
        var card = loanCard.getCard();
        var loan = loanCard.getLoan();

        DebitCardParams debitParam;

        if (cardType.equals(CardType.token))
            debitParam = new DebitCardParams(card.getToken(), card.getProcessing());
        else if (cardType.equals(CardType.pan))
            debitParam = new DebitCardParams(CardUtils.decryptPan(card.getPan()), card.getProcessing());
        else
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), "Card type not supported");

        var payPrams = new PayRequestDTO.PayParams(
                CardUtils.maskPanDigits(CardUtils.decryptPan(card.getPan())),
                card.getHolder(),
                loanCard.getPayer()
        );

        var notificationParams = new NotificationParams(
                card.getPhone(),
                card.getHolder(),
                loan.getId(),
                loan.getLanguage()
        );

        var payRequest = new PayRequestDTO(
                loanCard.getLoan().getExtId(),
                amount,
                debitParam,
                payPrams,
                addType,
                notificationParams
        );
        log.info("Prepared pay request: {}", payRequest);

        return paymentClientService.forcePay(payRequest);
    }

    private SimpleResponse<ResultData<PaymentForcePayResponseDTO>> sendRequestToPaymentServiceV3(Long amount, LoanCard loanCard,
                                                                                                 AddType addType, CardType cardType) {
        var card = loanCard.getCard();
        var loan = loanCard.getLoan();

        DebitCardParams debitParam;

        if (cardType.equals(CardType.token))
            debitParam = new DebitCardParams(card.getToken(), card.getProcessing());
        else if (cardType.equals(CardType.pan))
            debitParam = new DebitCardParams(CardUtils.decryptPan(card.getPan()), card.getProcessing());
        else
            return new SimpleResponse<>(ResultState.BlockOperation.getCode(), "Card type not supported");

        var payPrams = new PayRequestDTO.PayParams(
                CardUtils.maskPanDigits(CardUtils.decryptPan(card.getPan())),
                card.getHolder(),
                loanCard.getPayer()
        );

        var notificationParams = new NotificationParams(
                card.getPhone(),
                card.getHolder(),
                loan.getId(),
                loan.getLanguage()
        );

        var payRequest = new PayRequestDTO(
                loanCard.getLoan().getExtId(),
                amount,
                debitParam,
                payPrams,
                addType,
                notificationParams
        );
        log.info("Prepared pay request: {}", payRequest);

        return paymentClientService.forcePayV3(payRequest);
    }

    @Override
    public void alertedFromDavrMobile(CardCreditEventDTO eventDTO) {
        log.info("Begin checking loans which attached davr mobile card: {}", eventDTO.getCard());

        String pan = CardUtils.maskPanDigits(eventDTO.getCard());

        RLock lock = redissonClient.getLock("card-alert-to-debit by pan:" + pan);
        log.info("Try to lock card: {}", pan);

        boolean isLocked = false;
        try {
            // Пытаемся захватить блокировку на 5 секунд, с автоматическим освобождением через 30 сек
            isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_TTL, TimeUnit.MILLISECONDS);

            if (!isLocked) {
                log.error("Card: {} already handling!", pan);
                return;
            }

            davrMobileHandler(eventDTO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Occurred error while blocking with message: {}", e.getMessage());
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock(); // Освобождаем лок
            }
        }
    }

    private void davrMobileHandler(CardCreditEventDTO eventDTO) {
        String switcherName = "payment";

        var switcher = switcherRedisService.findByName(switcherName);

        if (switcher == null || !switcher.getIsEnabled()) {
            log.warn("Switcher for processing: {} is disabled and will be exited!", switcherName);
            return;
        }

        var cardType = CardType.getCardType(eventDTO.getCard());

        if (cardType == CardType.account) {
            var message = "Don't handle account types! Value is: %s".formatted(eventDTO.getCard());
            log.error("Method finished. Cause: {}", message);
            return;
        }

        var pcResult = CardCommon.detectProcessingByPan(eventDTO.getCard());

        if (!pcResult.getStatus()) {
            log.error("Method finished. Cause: {}", pcResult.getError().getMessage());
            return;
        }

        var pc = pcResult.getData();

        var card = eventDTO.getCard();

        if (cardType.equals(CardType.pan) && pc == Processing.UZCARD) {
            var p2pInfoResult = getP2PInfo(eventDTO.getCard());

            if (!p2pInfoResult.getStatus()) {
                log.error("Method finished. Cause: {}", p2pInfoResult.getError().getMessage());
                return;
            }

            if (p2pInfoResult.getData().getError() != null) {
                log.error("Method finished. Cause: {}", p2pInfoResult.getError().getMessage());
                return;
            }

            cardType = CardType.token;
            card = p2pInfoResult.getData().getResult().getId();
        }

        debitFromCard(card, eventDTO.getAmount(), AddType.ALERTED_DM, cardType);
    }

    @Override
    public void debitFromCard(String card, Long amount, AddType addType, CardType cardType) {
        log.info("---------- Starting to debit from card: card: {}, amount: {}, cardType: {} ----------", card, amount, cardType);
        log.info("Debit from card: {} with amount: {}", card, amount);

        if (card == null || card.isBlank() || cardType == null) {
            log.error("Force pay finished! Invalid card input. cardType: {}", cardType);
            return;
        }

        if (amount == null || amount <= 0) {
            log.error("Force pay finished! Received balance from davr mobile is {}", amount);
            return;
        }

        /**
         * Don't check cards token is approved, cause if card has in database, it's already approved
         */


        List<LoanCard> loanCards;

        switch (cardType) {
            case pan -> loanCards = loanCardDAO.findByCardPan(CardUtils.encryptPan(card));
            case token -> loanCards = loanCardDAO.findByCardToken(card);
            default -> {
                log.info("LoanCard repository not found for type: {}. Method finished!", cardType);
                return;
            }
        }

        if (loanCards == null || loanCards.isEmpty()) {
            log.info("Loans with card: {} is empty! Method finished!", card);
            return;
        }


        for (var loanCard : loanCards) {
            if (loanCard == null || loanCard.getLoan() == null || loanCard.getCard() == null) {
                log.error("LoanCard is incomplete and will be skipped. card: {}", card);
                continue;
            }

            log.info("Try to handle loanCard. extId: {}, loanId: {}, cardId: {}", loanCard.getLoan().getExtId(), loanCard.getLoan().getId(), loanCard.getCard().getId());

            if (Boolean.TRUE.equals(loanCard.getIsDeleted()) || !Boolean.TRUE.equals(loanCard.getIsConfirmed())) {
                log.error("Loan Card: {} is not active for loan: {}", card, loanCard.getLoan().getExtId());
                continue;
            }

            if (!Boolean.TRUE.equals(loanCard.getCard().getIsActive()) || !Boolean.TRUE.equals(loanCard.getCard().getIsConfirmed())) {
                log.error("Card: {} is not active for loan: {}", card, loanCard.getLoan().getExtId());
                continue;
            }

            if (Payer.EMPLOYEE.equals(loanCard.getPayer())) {
                log.error("LoanCard skipped. Cause: Payer type is Employee");
                continue;
            }

            var loan = loanCard.getLoan();

            if (!Boolean.TRUE.equals(loan.getIsAutoDebit())) {
                log.error("Loan does not participate in autoDebit: {}", loan.getExtId());
                continue;
            }

            var loanAmount = getLoanRecommendedAmount(loan.getExtId());

            if (loanAmount == null || loanAmount <= 0) {
                log.error(" ========= Loan amount not found or invalid for loan: {} =========", loan.getExtId());
                continue;
            }

            var debitAmount = resolveDebitAmountBeforePayment(amount, loanAmount, loanCard.getCard().getProcessing());

            if (debitAmount == null || debitAmount <= 0) {
                continue;
            }
            SimpleResponse<ResultData<PaymentForcePayResponseDTO>> paymentResponse = null;
            if (Processing.SVGATE_AUTOPAY == loanCard.getCard().getProcessing()) {
                var payAmount = Math.min(loanAmount, amount);
                try {
                    paymentResponse = sendRequestToPaymentServiceV3(payAmount, loanCard, addType, cardType);
                } catch (Exception e) {
                    log.error("ERROR_FROM_PAYMENT_SERVICE_TRANSPAY: {}", e.getMessage());
                }
                if (paymentResponse == null || paymentResponse.getError() != null || !Boolean.TRUE.equals(paymentResponse.getStatus()) || paymentResponse.getException() != null) {
                    log.info(" ------- SECOND_ATTEMPT_WITH_TRANSPAY_PURPOSE ----------");
                    paymentResponse = sendRequestToPaymentService(debitAmount, loanCard, addType, cardType);
                }
            } else {
                paymentResponse = sendRequestToPaymentService(debitAmount, loanCard, addType, cardType);
            }

            if (paymentResponse == null || !Boolean.TRUE.equals(paymentResponse.getStatus()) || paymentResponse.getData() == null) {
                log.error(" ========= Payment response throws error: {} ========= ", paymentResponse);
                continue;
            }

            if (!Boolean.TRUE.equals(paymentResponse.getData().getIsSuccess())) {
                log.error("Payment response is not success: {}", paymentResponse);

                var error = paymentResponse.getData().getError();
                var processing = loanCard.getCard().getProcessing();
                if (isInsufficientFunds(error, processing)) {
                    log.error("Card {} insufficient funds for {}. Stop card processing after loan: {}, errorCode: {}",
                            loanCard.getCard().getMaskedPan(), processing, loan.getExtId(), error.getCode());
                    continue;
                }

                log.error("Stop card processing after non-insufficient payment failure. loan: {}, card: {}, errorCode: {}, errorMessage: {}",
                        loan.getExtId(),
                        loanCard.getCard().getMaskedPan(),
                        error != null ? error.getCode() : null,
                        error != null ? error.getMessage() : null);
                break;
            }

            var paymentResult = paymentResponse.getData().getResult();

            if (paymentResult == null || paymentResult.getRemainingAmount() == null || paymentResult.getRemainingAmount() < 1) {
                log.error("Card balance is 0 and will be finished!");
                return;
            }

            amount = paymentResult.getRemainingAmount();

            log.info("Pay finished for loan: {} with response: {}", loan.getExtId(), paymentResult);
        }
    }

    private boolean isInsufficientFunds(CoreError error, Processing processing) {
        if (error == null || error.getCode() == null || processing == null) {
            return false;
        }

        if (Processing.HUMO == processing) {
            return HUMO_INSUFFICIENT_FUNDS_ERROR_CODE.equals(error.getCode());
        }

        if (Processing.UZCARD == processing || Processing.SVGATE_AUTOPAY == processing) {
            return UZCARD_INSUFFICIENT_FUNDS_ERROR_CODE.equals(error.getCode());
        }

        return false;
    }

    private Long getLoanRecommendedAmount(String loanExtId) {
        var loanInfoResponse = nciBillingService.getLoanInfo(loanExtId);

        if (loanInfoResponse == null) {
            log.error("Loan info response is null for loan: {}", loanExtId);
            return null;
        }

        if (loanInfoResponse.getError() != null) {
            log.error("Loan info response has error for loan: {} message: {}", loanExtId, loanInfoResponse.getError().getMessage());
            return null;
        }

        if (loanInfoResponse.getResult() == null) {
            log.error("Loan info result is null for loan: {}", loanExtId);
            return null;
        }

        var loanInfo = loanInfoResponse.getResult();
        var loanAmount = loanInfo.getRecommendedAmount();

        if (loanAmount == null) {
            log.error("Loan amount is null for loan: {}", loanExtId);
            return null;
        }

        return Math.max(loanAmount, 0L);
    }

    private Long resolveDebitAmountBeforePayment(Long cardBalance, Long loanAmount, Processing processing) {
        if (cardBalance == null || cardBalance <= 0) {
            return null;
        }

        if (loanAmount == null || loanAmount <= 0) {
            return null;
        }

        return Math.min(cardBalance, loanAmount);
    }

    public SimpleResponse<UzCardBaseResponse<P2PInfoResult>> getP2PInfo(String pan) {
        try {
            var request = new P2PInfoRequest(pan);

            var uzCardResult = uzCardService.getP2PInfo(request);
            log.info("P2P info result is: {}", uzCardResult);
            return new SimpleResponse<>(uzCardResult);
        } catch (Exception e) {
            log.error("P2P info result has error: {}", e.getMessage());
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), e.getMessage());
        }
    }
}
