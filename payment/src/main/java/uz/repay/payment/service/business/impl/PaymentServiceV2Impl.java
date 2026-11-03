package uz.repay.payment.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.payment.NCICourseResponse;
import uz.repay.common.model.dto.response.payment.PaymentForcePayResponseDTO;
import uz.repay.common.model.enums.*;
import uz.repay.common.utils.BranchUtils;
import uz.repay.common.utils.CardUtils;
import uz.repay.payment.config.aop.IsFraud;
import uz.repay.payment.config.aop.ReversePay;
import uz.repay.payment.model.dto.cardService.response.TerminalResponse;
import uz.repay.payment.model.dto.core.backlash.BacklashEventDTO;
import uz.repay.payment.model.dto.core.pay.reqeuest.ReverseRequestDTO;
import uz.repay.payment.model.dto.core.pay.response.PayResponseDTO;
import uz.repay.payment.model.dto.core.pay.response.result.DebitResult;
import uz.repay.payment.model.dto.nciBilling.request.NCIPayRequest;
import uz.repay.payment.model.dto.service.PayRequestScopedContext;
import uz.repay.payment.model.dto.service.ReverseRequestScopedContext;
import uz.repay.payment.model.entity.Bill;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.enums.ABSPayType;
import uz.repay.payment.model.enums.TranType;
import uz.repay.payment.model.property.InternalProperty;
import uz.repay.payment.service.business.*;
import uz.repay.payment.service.business.component.CommonHelper;
import uz.repay.payment.service.business.component.NotificationComponent;
import uz.repay.payment.service.core.TransactionDAO;
import uz.repay.payment.service.web.CardService;
import uz.repay.payment.service.web.NCIBillingService;
import uz.repay.payment.utils.BillUtils;
import uz.repay.payment.utils.ErrorBuilder;
import uz.repay.payment.utils.TranUtils;

import java.util.Objects;

import static uz.repay.common.model.enums.ResponseStatus.*;
import static uz.repay.payment.utils.TranUtils.validateStatus;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class PaymentServiceV2Impl implements PaymentServiceV2 {
    private final InternalProperty internalProperty;
    private final ApplicationEventPublisher eventPublisher;
    private final CommonHelper commonHelper;
    private final TransactionDAO transactionDAO;
    private final NCIBillingService nciBillingService;
    private final TemporaryService temporaryService;
    private final ReverseService reverseService;
    private final DebitTransService debitTransService;
    private final SyncService syncService;
    private final CardService cardService;
    private final ABSService absService;
    private final PCMaintenanceService pcMaintenanceService;
    private final NotificationComponent notificationComponent;
    private final static Integer UZCARD_PURPOSE_UNSUPPORTED_CODE = -350;
    private final static Integer UZCARD_TRANS_PAY_FALLBACK_METHOD = -2;
    private final static Integer SUCCESS_CODE = 202;
    private final PayRequestScopedContext payRequestScopedContext;
    private final ReverseRequestScopedContext reverseRequestScopedContext;


    @Override
    @IsFraud
    public ResultData<PayResponseDTO> payV2(@NonNull PayRequestDTO requestDTO) {
        var startTime = System.currentTimeMillis();
        log.info("Start pay transaction from: {} in {}", requestDTO, startTime);

        var result = startPay(requestDTO);

        validateRequests(requestDTO, startTime, result);

        return result;
    }


    @Override
    @IsFraud
    public ResultData<PayResponseDTO> payV3(@NonNull PayRequestDTO requestDTO, Integer method) {
        var startTime = System.currentTimeMillis();
        log.info("Start pay transaction from: {} in {}, method:{} ", requestDTO, startTime, method);

        var result = startPay(requestDTO, method);

        validateRequests(requestDTO, startTime, result);

        return result;
    }


    private void validateRequests(PayRequestDTO requestDTO, long startTime, ResultData<PayResponseDTO> result) {
        handleTransactionSpentTime(requestDTO, startTime);
    }


    private void handleTransactionSpentTime(PayRequestDTO requestDTO, long startTime) {
        var resultSpentTime = System.currentTimeMillis() - startTime;

        if (resultSpentTime > internalProperty.getPayment().getSuccessTime()) {
            log.warn("Loan: {} payment took a long time and {} processing will be disabled for {} minutes",
                    requestDTO.getLoanExtId(),
                    requestDTO.getDebitParams().getProcessing(),
                    internalProperty.getPayment().getDisabledTime());

            eventPublisher.publishEvent(
                    new BacklashEventDTO(
                            requestDTO.getLoanExtId(), resultSpentTime, requestDTO.getDebitParams().getProcessing(),
                            StatusType.TRANSACTION
                    )
            );
        }
    }

    public ResultData<PayResponseDTO> startPay(PayRequestDTO requestDTO) {
        return this.startPay(requestDTO, null);
    }

    public ResultData<PayResponseDTO> startPay(PayRequestDTO requestDTO, Integer method) {
        return startPayInternal(requestDTO, false);
    }

    private ResultData<PayResponseDTO> startPayWithTransPay(PayRequestDTO requestDTO) {
        return startPayInternal(requestDTO, true);
    }

    private ResultData<PayResponseDTO> startPayInternal(PayRequestDTO requestDTO, boolean retryWithTransPay) {
        var preparedTransaction = transactionBuilder(requestDTO);


        var debitBill = new Bill(
                requestDTO.getAmount(),
                requestDTO.getParams().getCardPan(),
                preparedTransaction,
                ResponseStatus.CREATED,
                TranType.DEBIT,
                TranUtils.takeCurrencyByPC(requestDTO.getDebitParams().getProcessing())
        );

        log.info("Saved debit bill: {}", debitBill);

        preparedTransaction.getBills().add(debitBill);

        var transaction = transactionDAO.save(preparedTransaction);

        if (CardUtils.isCurrencyPC(transaction.getProcessing())) {

            var currencyResponse = getNciCourse(transaction, debitBill);

            if (!currencyResponse.getStatus()) {
                return handleFailedStatus(transaction, ResponseStatus.FAILED, debitBill, currencyResponse.getError().getMessage(), 23);
            }


            var usdRate = currencyResponse.getData();
            log.info("USD conversation rate: {}", usdRate.getPurchase() / 100);

            transaction.setRate(usdRate.getPurchase() / 100);
        }

        var ePosResponse = commonHelper.getEPosForUzcardPayment(
                requestDTO.getDebitParams().getProcessing(),
                BranchUtils.getBranchByLoan(requestDTO.getLoanExtId()).getCode(),
                retryWithTransPay
        );

        debitBill.setResponse(ePosResponse.toMap());

        if (!ePosResponse.getStatus()) {
            return handleFailedStatus(transaction, ResponseStatus.FAILED, debitBill, ePosResponse.getError().getMessage(), 22);
        }

        var ePos = ePosResponse.getData();
        var extId = generateTransactionId(transaction);
        log.info("Generated externalId: {}", extId);


        transaction.setTerminalId(ePos.getTerminalId());
        debitBill.setTerminalId(ePos.getTerminalId());
        debitBill.setMerchantId(ePos.getMerchantId());
        debitBill.setStatus(ResponseStatus.DEBITING);
        debitBill.setCreditTo(ePos.getAccount());

        var debitMethod = retryWithTransPay ? UZCARD_TRANS_PAY_FALLBACK_METHOD : null;
        var debitResult = debitTransService.pay(requestDTO, extId, ePos, debitMethod);

        debitBill.setRequest(debitResult.getRequest());
        debitBill.setResponse(debitResult.getResponse());


        if (!debitResult.getIsSuccess()) {
            var failedResult = handleFailedStatus(transaction, debitResult.getError().getStatus(), debitBill, debitResult.getError().getMessage(), debitResult.getResultCode());
            if (shouldRetryUzcardDebitFromScratch(requestDTO, retryWithTransPay, debitResult)) {
                log.info("Uzcard trans.pay.purpose returned {}, retrying payment from scratch with trans.pay. branchId: {} loanId: {} original tranId: {}",
                        debitResult.getResultCode(), BranchUtils.getBranchByLoan(requestDTO.getLoanExtId()).getCode(), transaction.getLoanExtId(), transaction.getId());
                return startPayWithTransPay(requestDTO);
            }
            return failedResult;
        }


        var rrn = debitResult.getRrn();

        debitBill.setRefNum(debitResult.getPaymentID());
        debitBill.setStatus(ResponseStatus.SUCCESS);

        transaction.setRefNum(rrn);
        transaction.setStatus(SENDER_SUCCESS);
        transactionDAO.save(transaction);

        var creditTran = credit(
                requestDTO.getParams().getCardPan(),
                requestDTO.getDebitParams().getType(),
                ePos, transaction, debitResult.getPaymentID()
        );

        if (!creditTran.getIsSuccess())
            return creditTran;

        log.info("Transaction for loan: {} is finished with status: {}", requestDTO.getLoanExtId(), transaction.getStatus());

        sendNotificationToDebtor(requestDTO, transaction);

        PayResponseDTO responseDTO = new PayResponseDTO(transaction);

        log.info("Transaction is finished with dto: {}", responseDTO);
        return ResultData.ok(responseDTO);
    }


    private void sendNotificationToDebtor(PayRequestDTO requestDTO, Transaction t) {
        notificationComponent.sendNotificationToBroker(requestDTO.getNotificationParams().getPhone(),
                requestDTO.getNotificationParams().getFullName(),
                requestDTO.getLoanExtId(),
                requestDTO.getAmount(),
                requestDTO.getParams().getCardPan(),
                requestDTO.getDebitParams().getProcessing(),
                t.getCreatedBy(),
                requestDTO.getParams().getPayer(),
                requestDTO.getNotificationParams().getLanguage());

    }

    private static Transaction transactionBuilder(PayRequestDTO requestDTO) {
        var card = requestDTO.getDebitParams().getType() == CardType.token ? requestDTO.getDebitParams().getCard() :
                CardUtils.decryptPan(requestDTO.getDebitParams().getCard());

        log.info("Card for debiting: {}", card);

        return new Transaction(
                requestDTO.getLoanExtId(),
                requestDTO.getAmount(),
                requestDTO.getAddType(),
                requestDTO.getParams().getCardPan(),
                requestDTO.getParams().getCardOwner(),
                card,
                requestDTO.getDebitParams().getProcessing()
        );
    }

    private ResultData<PayResponseDTO> credit(String cardPan,
                                              CardType debitCardType,
                                              TerminalResponse ePos,
                                              Transaction transaction, String paymentID) {
        log.info("Begin crediting loan: {}", transaction.getLoanExtId());

        Long amount;

        if (CardUtils.isSumPC(transaction.getProcessing()))
            amount = transaction.getAmount();
        else
            amount = transaction.getAmount() * transaction.getRate();

        var preparedABSRequest = payRequestBuilder(transaction.getLoanExtId(), amount, cardPan, ePos, transaction.getRefNum(), generateTransactionId(transaction));


        var creditBill = new Bill(
                amount,
                transaction.getLoanExtId(),
                ePos.getAccount(),
                ePos.getTerminalId(),
                ePos.getMerchantId(),
                transaction,
                ResponseStatus.CREDITING,
                TranType.CREDIT,
                preparedABSRequest.toMap(),
                Currency.UZS
        );
        log.info("Saved credit bill: {}", creditBill);

        transaction.setStatus(ResponseStatus.CREDITING);
        transaction.getBills().add(creditBill);


        var absResponse = nciBillingService.pay(preparedABSRequest);
        creditBill.setResponse(absResponse.toMap());

        if (!absResponse.getStatus()) {
            return handleFailedStatus(transaction, ResponseStatus.NEED_CONFIRM, creditBill, absResponse.getError().getMessage(), 23);
        }

        if (absResponse.getData().getError() != null) {
            return cancel(paymentID, creditBill, transaction, ePos, absResponse.getData().getError().getMessage());
        }

        if (!Objects.equals(absResponse.getData().getResult().getCode(), SUCCESS_CODE)) {
            return cancel(paymentID, creditBill, transaction, ePos, "Success code not 202");
        }

        temporaryService.successSaveToTemporary(
                transaction,
                debitCardType,
                transaction.getProcessing(),
                transaction.getPayType()
        );

        creditBill.setStatus(ResponseStatus.SUCCESS);
        transaction.setStatus(SENDER_SUCCESS);
        transactionDAO.save(transaction);

        return ResultData.ok(
                new PayResponseDTO(
                        transaction
                )
        );
    }

    private ResultData<PayResponseDTO> cancel(String refNum, Bill creditBill, Transaction transaction, TerminalResponse ePos, String errorMessage) {
        log.info("Credit is failed and starting cancel method...");
        creditBill.setStatus(ResponseStatus.FAILED);
        transaction.setStatus(ResponseStatus.CANCELLING);
        transaction.setNote(errorMessage);
        transactionDAO.save(transaction);


        return cancelPayment(refNum, ePos, creditBill.getDebitFrom(), transaction);
    }

    private ResultData<PayResponseDTO> cancelPayment(String refNum, TerminalResponse ePos, String account, Transaction transaction) {
        reverseService.cancelPC(
                refNum,
                ePos.getMerchantId(),
                ePos.getTerminalId(),
                account,
                transaction
        );

        return new ResultData<>(ResultState.NotProcessed, "Transaction not processed in ABS API");
    }

    private static NCIPayRequest payRequestBuilder(String loanExtId, Long amount, String cardPan, TerminalResponse ePos, String rrn, String extId) {
        var preparedABSRequest = new NCIPayRequest(
                loanExtId,
                ePos.getTerminalId(),
                ePos.getMerchantId(),
                rrn,
                extId,
                amount,
                BillUtils.NCI_SUB_SYSTEM,
                cardPan,
                ePos.getAccount()
        );

        log.info("""
                        
                        PAY REQUEST:\s
                        
                        {
                            "paymentId": "{}",
                            "branch": "{}",
                            "formId": "{}",
                            "cardNum": "{}",
                            "refId": "{}",
                            "terminal": {
                                "terminalId": "{}",
                                "merchandId": "{}",
                                "account": "{}"
                            },
                            "amount": {},
                            "originator": 3636
                        }""", rrn,
                loanExtId.substring(0, 5),
                loanExtId.substring(5),
                cardPan,
                extId,
                ePos.getTerminalId(),
                ePos.getMerchantId(),
                ePos.getAccount(),
                amount);

        return preparedABSRequest;
    }

    private ResultData<PayResponseDTO> handleFailedStatus(Transaction transaction,
                                                          ResponseStatus status,
                                                          Bill bill, String message,
                                                          Integer resultCode) {
        log.info("Entered to failed method, tranId: {}", transaction.getId());
        transaction.setNote(message);
        transaction.setStatus(status);
        bill.setStatus(ResponseStatus.FAILED);
        transactionDAO.save(transaction);
        var errorMessage = "Transaction finished with error: %s".formatted(transaction.getNote());
        log.info(errorMessage);
        return new ResultData<>(resultCode, errorMessage);
    }

    private boolean shouldRetryUzcardDebitFromScratch(PayRequestDTO requestDTO, boolean retryWithTransPay, DebitResult debitResult) {
        return !retryWithTransPay
                && (requestDTO.getDebitParams().getProcessing().equals(Processing.UZCARD)
                || requestDTO.getDebitParams().getProcessing().equals(Processing.SVGATE_AUTOPAY))
                && isUzcardPurposeUnsupportedCode(debitResult.getResultCode());
    }

    private boolean isUzcardPurposeUnsupportedCode(Integer resultCode) {
        return Objects.equals(resultCode, UZCARD_PURPOSE_UNSUPPORTED_CODE)
                || Objects.equals(resultCode, -UZCARD_PURPOSE_UNSUPPORTED_CODE);
    }


    @Override
    @ReversePay
    public ResultData<PayResponseDTO> defaultReverseTransaction(@NonNull ReverseRequestDTO requestDTO) {
        log.info("Default reverse v2 started by request: {}", requestDTO.getTranId());

        var transaction = reverseRequestScopedContext.getTransaction();

        transaction.setReverseReason(requestDTO.getReason());

        var ePosResponse = commonHelper.getEPosForBranch(transaction.getProcessing(), transaction.getBranch().getCode());


        if (!ePosResponse.getStatus()) {
            return new ResultData<>(ResultState.InternalServerError, ePosResponse.getError().getMessage());
        }

        if (ePosResponse.getData() != null && !ePosResponse.getData().getTerminalId().equals(transaction.getTerminalId())) {
            ePosResponse = commonHelper.getEPosForUzcardPayment(transaction.getProcessing(), transaction.getBranch().getCode(), true);
        }

        if (!ePosResponse.getStatus()) {
            return new ResultData<>(ResultState.InternalServerError, ePosResponse.getError().getMessage());
        }

        var ePos = ePosResponse.getData();


        var debitBill = transaction
                .getBills()
                .stream()
                .filter(z -> z.getTranType().equals(TranType.DEBIT))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Debit bill not found for tranId: %d".formatted(requestDTO.getTranId())));

        if (!Objects.equals(ePos.getTerminalId(), debitBill.getTerminalId()) && !Objects.equals(ePos.getMerchantId(), debitBill.getMerchantId())) {
            return new ResultData<>(ResultState.BlockOperation,
                    "Actual ePos and transaction ePos are not the same. Actual: %s %s Transaction: %s %s"
                            .formatted(ePos.getTerminalId(), ePos.getMerchantId(), debitBill.getTerminalId(), debitBill.getMerchantId()));
        }

        if (!syncPayment(transaction, debitBill).getStatus())
            return new ResultData<>(ResultState.NotProcessed, "Sync failed");

        if (transaction.getGeneralId() == null)
            throw new InvalidDataException("General id is null");

        var creditReverseBillOptional =
                transaction
                        .getBills()
                        .stream()
                        .filter(t -> t.getTranType().equals(TranType.CREDIT_REVERSE)
                                && t.getStatus().equals(ResponseStatus.SUCCESS))
                        .findFirst();

        if (creditReverseBillOptional.isEmpty() || CardUtils.isCurrencyPC(transaction.getProcessing())) {
            // reverse credit
            reverseCreditABSPart(transaction, debitBill.getCreditTo());
        } else {
            log.warn("Credit reverse success bill not found in transaction: {}", transaction.getId());
        }
        log.info("ABS reverse credit method is finished!");


        var debitCancelBillOptional =
                transaction
                        .getBills()
                        .stream()
                        .filter(t -> t.getTranType().equals(TranType.DEBIT_CANCEL)
                                && t.getStatus().equals(ResponseStatus.SUCCESS))
                        .findFirst();

        var creditReverseBillAgainOptional =
                transaction
                        .getBills()
                        .stream()
                        .filter(t -> t.getTranType().equals(TranType.CREDIT_REVERSE)
                                && t.getStatus().equals(ResponseStatus.SUCCESS))
                        .findFirst();


        if (debitCancelBillOptional.isEmpty() && creditReverseBillAgainOptional.isPresent()) {
            // reverse debit

            reverseService.cancelPC(
                    debitBill.getRefNum(),
                    debitBill.getMerchantId(),
                    debitBill.getTerminalId(),
                    debitBill.getCreditTo(),
                    transaction
            );
        } else {
            log.warn("Debit cancel success bill found in transaction: {}", transaction.getId());
        }

        log.info("PC cancel debit method is finished!");

        return new ResultData<>(
                new PayResponseDTO(
                        transaction
                )
        );
    }

    private void reverseCreditABSPart(Transaction transaction, String creditTo) {

        var creditReverseBillOptional =
                transaction
                        .getBills()
                        .stream()
                        .filter(t -> t.getTranType().equals(TranType.CREDIT_REVERSE)
                                && t.getStatus().equals(ResponseStatus.SUCCESS))
                        .findFirst();

        log.info("Credit reverse success bill is Present: {}", creditReverseBillOptional.isPresent());

        if (creditReverseBillOptional.isEmpty())
            reverseService.reverseOnlyOneABSPaymentV2(transaction, creditTo);

        if (CardUtils.isCurrencyPC(transaction.getProcessing())) {
            reverseService.reverseTransitAndForex(transaction);
        }
    }


    private SimpleResponse<Transaction> syncPayment(Transaction transaction, Bill debitBill) {
        var extId = generateTransactionId(
                transaction
        );


        var syncPCPartResult = syncPCPart(transaction, debitBill, extId);
        if (!syncPCPartResult.getStatus())
            return syncPCPartResult;

        return syncPartABS(transaction, debitBill, extId);
    }

    private SimpleResponse<Transaction> syncPartABS(Transaction transaction, Bill debitBill, String extId) {
        log.info("Sync part ABS for tranId: {}", transaction.getId());

        var syncCreditPreparedBill = new Bill(
                transaction.getAmount(),
                debitBill.getCreditTo(),
                transaction.getLoanExtId(),
                extId,
                null,
                null,
                TranType.SYNCHRONIZE_ABS,
                ResponseStatus.CREATED,
                null,
                transaction,
                Currency.UZS
        );
        transaction.getBills().add(syncCreditPreparedBill);
        transactionDAO.save(transaction);


        var absPaymentSync = syncService.syncABS(transaction.getRefNum());

        var syncCreditBillOptional = BillUtils.getBillByStatus(transaction, TranType.SYNCHRONIZE_ABS, CREATED);

        if (syncCreditBillOptional.isEmpty())
            throw new GeneralErrorException("Logic is broken. Please talk with coder");


        var syncCreditBill = syncCreditBillOptional.get();

        syncCreditBill.setRequest(absPaymentSync.getRequest());
        syncCreditBill.setResponse(absPaymentSync.getResponse());


        if (!absPaymentSync.getIsSuccess()) {
            log.error("ABS sync failed and finished! Cause: {}", absPaymentSync.getError().getMessage());
            syncCreditBill.setStatus(ResponseStatus.FAILED);
            transaction.setStatus(NEED_CONFIRM);

            if (!absPaymentSync.getStatus().equals(NEED_CONFIRM)) {


                if (absPaymentSync.getError().getCode().equals(404))
                    completeTransaction(transaction.getId());
                else {
                    transaction.setNote(absPaymentSync.getError().getMessage());
                    transaction.setStatus(FAILED);

                    reverseService.cancelPC(transaction.getRefNum(), debitBill.getMerchantId(), debitBill.getTerminalId(), debitBill.getCreditTo(), transaction);
                }


            }


            transactionDAO.save(transaction);
            return new SimpleResponse<>(absPaymentSync.getError().getCode(), absPaymentSync.getError().getMessage());
        }


        syncCreditBill.setRefNum(absPaymentSync.getRRN());
        syncCreditBill.setStatus(ResponseStatus.SUCCESS);

        transaction.setGeneralId(absPaymentSync.getRRN());
        transaction.setStatus(ResponseStatus.SUCCESS);
        transactionDAO.save(transaction);

        return new SimpleResponse<>(transaction);
    }

    private SimpleResponse<Transaction> syncPCPart(Transaction transaction, Bill debitBill, String extId) {
        log.info("Sync Processing part for tranId: {}", transaction.getId());

        var isSumPC = CardUtils.isSumPC(transaction.getProcessing());

        var syncDebitPreparedBill = new Bill(
                transaction.getAmount(),
                debitBill.getDebitFrom(),
                debitBill.getCreditTo(),
                extId,
                debitBill.getMerchantId(),
                debitBill.getTerminalId(),
                TranType.SYNCHRONIZE_PC,
                ResponseStatus.CREATED,
                null,
                transaction,
                isSumPC ? Currency.UZS : Currency.USD
        );

        transaction
                .getBills()
                .add(syncDebitPreparedBill);
        transactionDAO.save(transaction);

        var syncDebitBillOptional = BillUtils.getBillByStatus(transaction, TranType.SYNCHRONIZE_PC, CREATED);

        if (syncDebitBillOptional.isEmpty()) {
            var message = "Not found sync debit bill with tranType: %s and status: %s".formatted(TranType.SYNCHRONIZE_PC, CREATED);
            log.error(message);
            return new SimpleResponse<>(1045, message);
        }

        var syncDebitBill = syncDebitBillOptional.get();

        var syncResult = syncService.syncPC(extId, transaction);


        var refNum = syncResult.getRRN() != null ? syncResult.getRRN() : null;
        transaction.setRefNum(refNum);
        syncDebitBill.setRequest(syncResult.getRequest());
        syncDebitBill.setRefNum(refNum);
        syncDebitBill.setResponse(syncResult.getResponse());
        syncDebitBill.setStatus(syncResult.getStatus());

        if (!syncResult.getIsSuccess()) {
            log.error("Payment sync failed and finished! Cause: {}", syncResult.getError().getMessage());


            if (!syncResult.getStatus().equals(NEED_CONFIRM)) {
                if (syncResult.getRRN() != null)
                    syncDebitBill.setRefNum(syncResult.getRRN());

                transaction.setStatus(syncResult.getStatus());
                var errorMessageResult = cardService.findAllStatus();
                String errorMessage = ErrorBuilder.getErrorCodeDescription(errorMessageResult, syncResult.getError().getCode().toString(), transaction.getProcessing());
                transaction.setNote(errorMessage);

                switch (syncResult.getStatus()) {
                    case DEBIT_CONFIRM_NEED_CONFIRM -> syncDebitBill.setStatus(NEED_CONFIRM);
                    case REVERSED, CANCELLED -> syncDebitBill.setStatus(SUCCESS);
                    default -> syncDebitBill.setStatus(FAILED);
                }

            }

            transactionDAO.save(transaction);
            return new SimpleResponse<>(syncResult.getError().getCode(), syncResult.getError().getMessage());
        }

        syncDebitBill.setRefNum(syncResult.getRRN());
        syncDebitBill.setStatus(ResponseStatus.SUCCESS);
        transaction.setStatus(ResponseStatus.SUCCESS);
        transactionDAO.save(transaction);
        return new SimpleResponse<>(transaction);
    }

    private String generateTransactionId(Transaction transaction) {
        return internalProperty.getPayment().getExternalPrefix().formatted(transaction.getId());
    }

    @Override
    public ResultData<PayResponseDTO> synchronize(@NonNull Long tranId) {
        log.info("Sync request pre handle >>>{}", tranId);

        var transaction = transactionDAO.findById(tranId);

        TranUtils.checkTranDate(transaction);

        validateStatus(transaction.getStatus());

        var debitBill = transaction
                .getBills()
                .stream()
                .filter(z -> z.getTranType().equals(TranType.DEBIT))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Transaction: #%d not found Debit Bill".formatted(tranId)));

        syncPayment(transaction, debitBill);


        return new ResultData<>(
                new PayResponseDTO(
                        transaction
                )
        );
    }

    @Override
    @ReversePay
    public ResultData<PayResponseDTO> reverseCredit(@NonNull ReverseRequestDTO requestDTO) {
        log.info("Reverse ABS transaction request pre handle >>>{}", requestDTO);

        var transaction = reverseRequestScopedContext.getTransaction();

        checkToDuplicate(transaction, TranType.CREDIT_REVERSE, "Credit reverse for tranId: {} already defined", "Credit reverse already has with success status!");


        var debitBill = transaction
                .getBills()
                .stream()
                .filter(z -> z.getTranType().equals(TranType.DEBIT) && z.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Transaction: #%d not found Debit Bill".formatted(requestDTO.getTranId())));

        transaction.setReverseReason(requestDTO.getReason());


        transaction
                .getBills()
                .stream()
                .filter(z -> z.getTranType().equals(TranType.CREDIT))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Transaction: #%d not found Credit Bill".formatted(requestDTO.getTranId())));


        reverseCreditABSPart(transaction, debitBill.getCreditTo());


        return new ResultData<>(
                new PayResponseDTO(
                        transaction
                )
        );
    }

    private static void checkToDuplicate(Transaction transaction, TranType tranType, String s, String message) {
        var creditReverseBill = transaction
                .getBills()
                .stream()
                .filter(t -> t.getTranType().equals(tranType) && t.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst();

        if (creditReverseBill.isPresent()) {
            log.info(s, transaction.getId());
            throw new GeneralErrorException(message);
        }
    }

    @Override
    @ReversePay
    public ResultData<PayResponseDTO> cancelDebit(@NonNull ReverseRequestDTO requestDTO) {
        log.info("Reverse PC payment request pre handle >>>{}", requestDTO);

        var transaction = reverseRequestScopedContext.getTransaction();

        checkToDuplicate(transaction, TranType.DEBIT_CANCEL, "Debit cancel for tranId: {} already defined", "Debit cancel already has with success status!");


        var debitBill = transaction
                .getBills()
                .stream()
                .filter(z -> z.getTranType().equals(TranType.DEBIT))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Transaction: #%d not found Debit Bill".formatted(requestDTO.getTranId())));

        transaction.setReverseReason(requestDTO.getReason());

        String refNum;

        if (!debitBill.getStatus().equals(ResponseStatus.SUCCESS))
            refNum = transaction
                    .getBills()
                    .stream()
                    .filter(z -> z.getTranType().equals(TranType.SYNCHRONIZE_PC) && z.getStatus().equals(ResponseStatus.SUCCESS))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Transaction: #%d not found Sync Bill! Please synchronize firstly".formatted(requestDTO.getTranId())))
                    .getRefNum();
        else {
            refNum = debitBill.getRefNum();
        }


        reverseService.cancelPC(refNum, debitBill.getMerchantId(), debitBill.getTerminalId(), debitBill.getCreditTo(), transaction);

        return new ResultData<>(
                new PayResponseDTO(
                        transaction
                )
        );
    }


    @Override
    public ResultData<PayResponseDTO> completeTransaction(@NotNull Long tranId) {
        log.info("Taken request to complete transaction with id: {}", tranId);

        var transaction = transactionDAO.findById(tranId);

        validateStatus(transaction.getStatus());


        switch (transaction.getProcessing()) {
            case UZCARD, HUMO, SVGATE_AUTOPAY, DAVR_MASTER_CARD -> {
                return completeForUzCardAndHuMoPC(transaction);
            }
            case DAVR_VISA -> {
                return completeForDAVRVisaPC(transaction);
            }
            default ->
                    throw new GeneralErrorException("For PC: %s not found complete function".formatted(transaction.getProcessing()));

        }
    }

    private ResultData<PayResponseDTO> completeForUzCardAndHuMoPC(Transaction transaction) {
        log.info("Complete transaction for processing: {}", transaction.getProcessing());
        var tranId = transaction.getId();

        //todo change logic (add transit and forex check and revert logic)
        /**

         If transaction status not SUCCESS, CANCELLED, REVERSED, SENDER_SUCCESS, RECIPIENT_SUCCESS
         can start transaction completing operation.

         1. If it has DEBIT_CANCEL or CREDIT_REVERSE transaction type
         a) check tran type CREDIT_REVERSE by status priority
         - if status = FAILED, check transaction from ABS by extId
         - if transaction not found, reverse again and start debit_cancel operation
         - If found, to see 1.b paragraph.
         b) check tran type DEBIT_CANCEL by status priority
         - if status = FAILED, check transaction from PC, else save bill with SUCCESS and transaction status with CANCELLED statuses
         - If not cancelled, cancel it
         - If cancelled, save bill with SUCCESS and transaction status with CANCELLED statuses


         2. If Credit Success or Synchronize ABS Success (means credit success)


         3. If Credit Failed ->


         3. If Debit Success or Synchronize PC Success (means debit success)
         a) if Credit is success
         - If processing = HUMO check payment status
         * If payment is active confirm it and save transaction and bill with SUCCESS status
         * If payment is confirmed save transaction and bill with SUCCESS status
         b) if Credit is not found "START CREDIT"

         4. If Debit Failed -> Check status from PC
         a) if payment is success, check Credit type bill, if not found "START CREDIT"
         b) if success check Credit type bill, if found, check from ABS
         - If found in ABS, see 3.a paragraph.
         - If not found "START CREDIT"
         else -> save bill and transaction status FAILED



         P.S - if when checking transaction from ABS or PC taking timeOut error, don't change transaction state!
         Exit without any changing, only save bill with request and response

         "START CREDIT" - start credit (send to abs and save to temporary)
         */


        var debitOrSyncBill = transaction
                .getBills()
                .stream()
                .filter(t -> (t.getTranType().equals(TranType.DEBIT) || t.getTranType().equals(TranType.SYNCHRONIZE_PC)) && t.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Success transaction in %d with DEBIT or SYNC bill not found".formatted(tranId)));


        var creditBillHas = transaction
                .getBills()
                .stream()
                .anyMatch(c -> c.getTranType().equals(TranType.CREDIT) && !c.getStatus().equals(ResponseStatus.FAILED));

        if (!creditBillHas) {
            if (creditTransactionCheck(transaction, debitOrSyncBill)) {
                log.info("Complete transaction sent payment to ABS and finished");
                return ResultData.ok(new PayResponseDTO(transaction));
            }
        }


        checkDebitConfirm(transaction, debitOrSyncBill);

        transaction.setStatus(SUCCESS);
        transactionDAO.save(transaction);

        log.info("Complete transaction is finished");
        return ResultData.ok(new PayResponseDTO(transaction));
    }

    private void checkDebitConfirm(Transaction transaction, Bill debitOrSyncBill) {
        if (transaction.getProcessing().equals(Processing.HUMO)) {
            var debitConfirmBillOptional = transaction
                    .getBills()
                    .stream()
                    .filter(t -> t.getTranType().equals(TranType.DEBIT_CONFIRM) && t.getStatus().equals(ResponseStatus.SUCCESS))
                    .findFirst();

            if (debitConfirmBillOptional.isEmpty()) {
                var syncResult = syncService.syncHuMo(generateTransactionId(transaction));
                transaction.setStatus(syncResult.getStatus());


                if (syncResult.getStatus().equals(DEBIT_CONFIRM_NEED_CONFIRM)) {

                    debitTransService.huMoDebitConfirm(
                            debitOrSyncBill,
                            transaction
                    );

                }

            }

            transactionDAO.save(transaction);
        }
    }

    private boolean creditTransactionCheck(Transaction transaction, Bill debitOrSyncBill) {
        var nciResponse = nciBillingService.checkPaymentFromABS(transaction.getRefNum());

        if (!nciResponse.getStatus())
            throw new GeneralErrorException(nciResponse.getError().getMessage() + " << Please try again later!");


        if (nciResponse.getData().getResult() != null) {
            if (nciResponse.getData().getResult().getCode() == 200) {

                var creditBillOptional = transaction
                        .getBills()
                        .stream()
                        .filter(z -> z.getTranType().equals(TranType.CREDIT))
                        .findFirst();

                if (creditBillOptional.isPresent()) {
                    var creditBill = creditBillOptional.get();

                    String generalId = nciResponse.getData().getResult().getGeneralId();

                    creditBill.setRefNum(generalId);
                    creditBill.setResponse(nciResponse.toMap());
                    creditBill.setStatus(ResponseStatus.SUCCESS);
                    transaction.setGeneralId(generalId);
                    transaction.setStatus(ResponseStatus.SUCCESS);
                    transactionDAO.save(transaction);
                }

                return false;
            }
        }


        var creditTran = startCreditHandle(transaction, debitOrSyncBill);

        if (!creditTran.getStatus())
            throw new GeneralErrorException(creditTran.getError().getMessage());


        return true;
    }


    private SimpleResponse<NCICourseResponse> getNciCourse(Transaction transaction, Bill simpleTranBill) {
        var currencyResponse = nciBillingService.getCourse();

        if (!currencyResponse.getStatus()) {
            simpleTranBill.setStatus(ResponseStatus.FAILED);
            transaction.setNote(currencyResponse.getError().getMessage());
            transaction.setStatus(ResponseStatus.FAILED);
            transactionDAO.save(transaction);
            return new SimpleResponse<>(
                    638,
                    currencyResponse.getError().getMessage()
            );
        }

        if (currencyResponse.getData().getError() != null) {
            simpleTranBill.setStatus(ResponseStatus.FAILED);
            transaction.setNote(currencyResponse.getData().getError().getMessage());
            transaction.setStatus(ResponseStatus.FAILED);
            transactionDAO.save(transaction);
            return new SimpleResponse<>(
                    639,
                    currencyResponse.getData().getError().getMessage()
            );
        }

        var usdRateOptional = currencyResponse
                .getData()
                .getResult()
                .stream()
                .filter(z -> z.getAbbr().equals(Currency.USD.name()))
                .findFirst();

        if (usdRateOptional.isEmpty()) {
            simpleTranBill.setStatus(ResponseStatus.FAILED);
            transaction.setNote("USD object is empty in course response");
            transaction.setStatus(ResponseStatus.FAILED);
            transactionDAO.save(transaction);
            return new SimpleResponse<>(
                    639,
                    "USD object is empty in course response"
            );
        }

        return new SimpleResponse<>(usdRateOptional.get());
    }

    private ResultData<PayResponseDTO> completeForDAVRVisaPC(Transaction transaction) {
        log.info("Complete transaction for processing: {}", transaction.getProcessing());
        var tranId = transaction.getId();

        // 1. check debit (from card)
        // 2. check credit (to loan 29%)
        // 3. check forex operation
        // 4. check simple pay

        var debitOrSyncBill = transaction
                .getBills()
                .stream()
                .filter(t -> (t.getTranType().equals(TranType.DEBIT) || t.getTranType().equals(TranType.SYNCHRONIZE_PC)) && t.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Success transaction in %d with DEBIT or SYNC bill not found".formatted(tranId)));

        var creditBillOptional = transaction
                .getBills()
                .stream()
                .filter(t -> t.getTranType().equals(TranType.CREDIT) && t.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst();

        if (creditBillOptional.isEmpty()) {
            log.info("Credit SUCCESS bill is empty and will be do it again..");

            var creditTran = startCreditHandle(transaction, debitOrSyncBill);

            if (!creditTran.getStatus())
                return new ResultData<>(
                        ResultState.NotProcessed,
                        creditTran.getError().getMessage()
                );
        }


        var forexBillOptional = transaction
                .getBills()
                .stream()
                .filter(t -> t.getTranType().equals(TranType.FOREX) && t.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst();

        if (forexBillOptional.isEmpty()) {
            var forexTran = startForexHandle(transaction);


            if (!forexTran.getStatus()) {
                transaction.setStatus(NEED_CONFIRM);
                transactionDAO.save(transaction);

                return new ResultData<>(
                        ResultState.NotProcessed,
                        forexTran.getError().getMessage()
                );
            }
        }


        var transitBillOptional = transaction
                .getBills()
                .stream()
                .filter(t -> t.getTranType().equals(TranType.TRANSIT) && t.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst();

        if (transitBillOptional.isEmpty()) {
            log.info("Transit SUCCESS bill is empty and will be do it again..");

            var transitTran = startTransitHandle(transaction);

            if (!transitTran.getStatus())
                return new ResultData<>(
                        ResultState.NotProcessed,
                        transitTran.getError().getMessage()
                );
        }

        log.info("Complete transaction is finished");
        return ResultData.ok(new PayResponseDTO(transaction));
    }

    private SimpleResponse<ResultData<PayResponseDTO>> startForexHandle(Transaction transaction) {
        log.info("Forex SUCCESS bill is empty and will be do it again..");

        var absTransactionsOptional = pcMaintenanceService.findByProcessing(transaction.getProcessing());

        if (absTransactionsOptional.isEmpty() ||
                absTransactionsOptional.get()
                        .getAbsSettings()
                        .stream()
                        .filter(z -> z.getPayType().equals(ABSPayType.FOREX_RESIDENT))
                        .findFirst()
                        .isEmpty()) {
            log.info("PC maintenance settings not found for processing: {}", transaction.getProcessing());

            return new SimpleResponse<>(
                    7002,
                    "PC maintenance settings not found for FOREX"

            );
        }

        //checked get method below
        var absForexTransaction = absTransactionsOptional.get()
                .getAbsSettings()
                .stream()
                .filter(z -> z.getPayType().equals(ABSPayType.FOREX_RESIDENT))
                .findFirst()
                .get();


        var absResult = absService.transaction(transaction, absForexTransaction);

        if (!absResult.getStatus())
            return new SimpleResponse<>(
                    7003,
                    absResult.getError().getMessage()
            );

        return new SimpleResponse<>(
                ResultData.ok(
                        new PayResponseDTO(
                                transaction
                        )
                )
        );
    }

    private SimpleResponse<ResultData<PayResponseDTO>> startCreditHandle(Transaction transaction, Bill debitOrSyncBill) {
        var ePosResponse = commonHelper.getEPosForBranch(
                transaction.getProcessing(),
                transaction.getBranch().getCode()
        );


        if (!ePosResponse.getStatus()) {
            throw new GatewayException(ePosResponse.getError().getMessage());
        }

        var ePos = ePosResponse.getData();

        var creditTran = credit(
                debitOrSyncBill.getDebitFrom(),
                CardType.pan, // This means nothing and not used anywhere
                ePos, transaction, debitOrSyncBill.getRefNum()
        );

        if (!creditTran.getIsSuccess())
            return new SimpleResponse<>(
                    7000,
                    creditTran.getError().getMessage()
            );

        return new SimpleResponse<>(creditTran);
    }

    @SuppressWarnings("all")
    private SimpleResponse<ResultData<PayResponseDTO>> startTransitHandle(Transaction transaction) {
        var absTransactionsOptional = pcMaintenanceService.findByProcessing(transaction.getProcessing());

        if (absTransactionsOptional.isEmpty() ||
                absTransactionsOptional.get()
                        .getAbsSettings()
                        .stream()
                        .filter(z -> z.getPayType().equals(ABSPayType.SIMPLE_PAY))
                        .findFirst()
                        .isEmpty()) {
            log.info("PC maintenance settings not found for processing: {}", transaction.getProcessing());

            return new SimpleResponse<>(
                    7002,
                    "PC maintenance settings not found for SIMPLE_PAY"

            );
        }

        //checked get method below
        var absSimplePayTransaction = absTransactionsOptional.get()
                .getAbsSettings()
                .stream()
                .filter(z -> z.getPayType().equals(ABSPayType.SIMPLE_PAY))
                .findFirst()
                .get();


        var absResult = absService.transaction(transaction, absSimplePayTransaction);


        if (!absResult.getStatus()) {
            transaction.setStatus(NEED_CONFIRM);
            transactionDAO.save(transaction);
            return new SimpleResponse<>(
                    7003,
                    absResult.getError().getMessage()
            );
        }


        transaction.setStatus(ResponseStatus.SUCCESS);
        transactionDAO.save(transaction);


        return new SimpleResponse<>(
                ResultData.ok(
                        new PayResponseDTO(
                                transaction
                        )
                )
        );
    }


    @Override
    @IsFraud
    public ResultData<PaymentForcePayResponseDTO> forcePay(@NonNull PayRequestDTO requestDTO) {
        var startTime = System.currentTimeMillis();
        log.info("Received command to force pay transaction from: {} in {}", requestDTO, startTime);

        String loanExtId = requestDTO.getLoanExtId();

        LoanAdditionalResponseDTO loanInfo = payRequestScopedContext.getLoanAdditionalResponseDTO();

        var recommendedAmount = loanInfo != null ? loanInfo.getRecommendedAmount() : 0L;

        if (recommendedAmount <= 0) {
            var message = "Loan recommended amount is %d!".formatted(recommendedAmount);
            log.error("Finished command to force pay with error result: {}", message);
            return new ResultData<>(ResultState.LOAN_DEBT_CLOSED, message);
        }

        log.info("Recommended amount is: {}", recommendedAmount);

        var remainingAmount = 0L;

        if (requestDTO.getAmount() > recommendedAmount) {
            log.warn("Debit amount {} changed, cause recommended amount is: {}", requestDTO.getAmount(), recommendedAmount);
            remainingAmount = requestDTO.getAmount() - recommendedAmount;
            requestDTO.setAmount(recommendedAmount);
        }


        var result = startPay(requestDTO);

        validateRequests(requestDTO, startTime, result);


        if (!result.getIsSuccess()) {
            log.error("Finished command to force pay with error result: {}", result.getError().getMessage());

            Processing processing = requestDTO.getDebitParams().getProcessing();
            Integer code = result.getError().getCode();

            var pcNotFraudCodes = ErrorBuilder.notFraudCodes()
                    .get(processing);

            if (pcNotFraudCodes != null) {
                if (pcNotFraudCodes.contains(code)) {
                    log.info("Found not fraud code: {} for pc: {}", code, processing);
                    return new ResultData<>(ResultState.INSUFFICIENT_FUNDS, result.getError().getMessage());
                }
            }

            return new ResultData<>(result.getError(), false);
        }

        var response = ResultData.ok(
                new PaymentForcePayResponseDTO(
                        remainingAmount,
                        loanExtId,
                        Currency.UZS,
                        requestDTO.getDebitParams()
                )
        );

        log.info("Finished command to force pay with response: {}", response);
        return response;
    }

    @Override
    @IsFraud
    public ResultData<PaymentForcePayResponseDTO> forcePayV3(@NonNull PayRequestDTO requestDTO, Integer method) {
        var startTime = System.currentTimeMillis();
        log.info("Received command to force pay transaction from: {} in {}", requestDTO, startTime);

        String loanExtId = requestDTO.getLoanExtId();

        LoanAdditionalResponseDTO loanInfo = payRequestScopedContext.getLoanAdditionalResponseDTO();

        var recommendedAmount = loanInfo.getRecommendedAmount();

        if (recommendedAmount <= 0) {
            var message = "Loan recommended amount is %d!".formatted(recommendedAmount);
            log.error("Finished command to force pay with error result: {}", message);
            return new ResultData<>(ResultState.LOAN_DEBT_CLOSED, message);
        }

        log.info("Recommended amount is: {}", recommendedAmount);

        var remainingAmount = 0L;

        if (requestDTO.getAmount() > recommendedAmount) {
            log.warn("Debit amount {} changed, cause recommended amount is: {}", requestDTO.getAmount(), recommendedAmount);
            remainingAmount = requestDTO.getAmount() - recommendedAmount;
            requestDTO.setAmount(recommendedAmount);
        }


        var result = startPay(requestDTO, method);

        validateRequests(requestDTO, startTime, result);


        if (!result.getIsSuccess()) {
            log.error("Finished command to force pay with error result: {}", result.getError().getMessage());

            Processing processing = requestDTO.getDebitParams().getProcessing();
            Integer code = result.getError().getCode();

            var pcNotFraudCodes = ErrorBuilder.notFraudCodes()
                    .get(processing);

            if (pcNotFraudCodes != null) {
                if (pcNotFraudCodes.contains(code)) {
                    log.info("Found not fraud code: {} for pc: {}", code, processing);
                    return new ResultData<>(ResultState.INSUFFICIENT_FUNDS, result.getError().getMessage());
                }
            }

            return new ResultData<>(result.getError(), false);
        }

        var response = ResultData.ok(
                new PaymentForcePayResponseDTO(
                        remainingAmount,
                        loanExtId,
                        Currency.UZS,
                        requestDTO.getDebitParams()
                )
        );

        log.info("Finished command to force pay with response: {}", response);
        return response;
    }

}
