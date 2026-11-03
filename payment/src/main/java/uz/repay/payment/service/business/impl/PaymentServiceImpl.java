package uz.repay.payment.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.payment.PaymentForcePayResponseDTO;
import uz.repay.common.model.enums.*;
import uz.repay.common.utils.CardUtils;
import uz.repay.payment.config.aop.IsFraud;
import uz.repay.payment.model.dto.billing.request.CancelRequest;
import uz.repay.payment.model.dto.billing.request.CheckRequest;
import uz.repay.payment.model.dto.billing.request.PayRequest;
import uz.repay.payment.model.dto.core.backlash.BacklashEventDTO;
import uz.repay.payment.model.dto.core.pay.reqeuest.ReverseRequestDTO;
import uz.repay.payment.model.dto.core.pay.response.PayResponseDTO;
import uz.repay.payment.model.dto.nciBilling.request.NCIReverseDTO;
import uz.repay.payment.model.dto.service.PayRequestScopedContext;
import uz.repay.payment.model.entity.Bill;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.enums.PayType;
import uz.repay.payment.model.enums.TranType;
import uz.repay.payment.model.property.BillingProperty;
import uz.repay.payment.model.property.InternalProperty;
import uz.repay.payment.service.business.PaymentService;
import uz.repay.payment.service.business.TemporaryService;
import uz.repay.payment.service.business.component.CommonHelper;
import uz.repay.payment.service.core.TransactionDAO;
import uz.repay.payment.service.web.BillingService;
import uz.repay.payment.service.web.NCIBillingService;
import uz.repay.payment.utils.BillUtils;
import uz.repay.payment.utils.ErrorBuilder;
import uz.repay.payment.utils.TranUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static uz.repay.payment.utils.BillUtils.billBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class PaymentServiceImpl implements PaymentService {

    private final BillingProperty billingProperty;
    private final TransactionDAO transactionDAO;
    private final BillingService billingService;
    private final TemporaryService temporaryService;
    private final NCIBillingService nciBillingService;
    private final CommonHelper commonHelper;
    private final InternalProperty internalProperty;
    private final ApplicationEventPublisher eventPublisher;
    private final PayRequestScopedContext payRequestScopedContext;

    @Override
    @IsFraud
    public ResultData<PayResponseDTO> pay(@NonNull PayRequestDTO requestDTO) {
        var startTime = System.currentTimeMillis();
        log.info("Start pay transaction from: {} in {}", requestDTO, startTime);

        commonHelper.checkSwitcherIsEnabled();

        var result = startPay(requestDTO);

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

        return result;
    }

    @Override
    @IsFraud
    public ResultData<PaymentForcePayResponseDTO> forcePay(@NonNull PayRequestDTO requestDTO) {
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

        log.info(" New_startPay_method_call ");
        var preparedTransaction = new Transaction(
                requestDTO.getLoanExtId(),
                requestDTO.getParams().getCardPan(),
                requestDTO.getParams().getCardOwner(),
                requestDTO.getAmount(),
                requestDTO.getDebitParams().getProcessing(),
                requestDTO.getAddType()
        );


        var checkBill = new Bill(
                requestDTO.getAmount(),
                requestDTO.getParams().getCardPan(),
                preparedTransaction
        );
        preparedTransaction.getBills().add(checkBill);

        var transaction = transactionDAO.save(preparedTransaction);

        var preparedCheckRequestResult = checkRequestBuilder(requestDTO, checkBill);

        if (!preparedCheckRequestResult.getStatus()) {
            return saveFailTransaction(ResponseStatus.CHECK_FAIL, transaction,
                    preparedCheckRequestResult.getError().getMessage());
        }

        var preparedCheckRequest = preparedCheckRequestResult.getData();
        transactionDAO.save(transaction);

        var checkResponse = billingService.check(preparedCheckRequest);


        if (!checkResponse.getStatus()) {
            checkBill.setStatus(ResponseStatus.FAILED);
            checkBill.setResponse(checkResponse.toMap());
            return saveFailTransaction(ResponseStatus.CHECK_FAIL, transaction, checkResponse.getError().getMessage());
        }

        if (!checkResponse.getData().getSuccess()) {
            checkBill.setStatus(ResponseStatus.FAILED);
            checkBill.setResponse(checkResponse.toMap());
            checkBill.setBillingId(checkResponse.getData().getId());
            return saveFailTransaction(ResponseStatus.CHECK_FAIL, transaction, checkResponse.getData().getResponseText());
        }

        checkBill.setStatus(checkResponse.getData().getStatus());
        checkBill.setResponse(checkResponse.getData().toMap());
        checkBill.setBillingId(checkResponse.getData().getId());
        transaction.setStatus(ResponseStatus.CHECK_OK);
        transactionDAO.save(transaction);

        var preparedPayRequestResponse = payRequestBuilder(requestDTO,
                String.valueOf(checkResponse.getData().getId()),
                transaction);

        if (!preparedPayRequestResponse.getStatus()) {
            return saveFailTransaction(ResponseStatus.FAILED, transaction, preparedPayRequestResponse.getError().getMessage());
        }


        var preparedPayRequest = preparedPayRequestResponse.getData();

        var payBill = new Bill(
                preparedPayRequest,
                checkBill,
                transaction.getLoanExtId()
        );


        log.info(" ------- Request before calling billing -------");
        var paySimpleResponse = billingService.pay(preparedPayRequest);
        transaction.getBills().add(payBill);


        if (!paySimpleResponse.getStatus()) {
            payBill.setStatus(ResponseStatus.FAILED);
            payBill.setResponse(paySimpleResponse.toMap());
            return saveFailTransaction(ResponseStatus.NEED_CONFIRM, transaction, paySimpleResponse.getError().getMessage());
        }

        if (!paySimpleResponse.getData().getSuccess()) {
            payBill.setStatus(ResponseStatus.FAILED);
            payBill.setResponse(paySimpleResponse.toMap());
            return saveFailTransaction(ResponseStatus.FAILED, transaction, paySimpleResponse.getData().getResponseText());
        }

        var payResponse = paySimpleResponse.getData();


        payBill.setStatus(ResponseStatus.SUCCESS);
        payBill.setResponse(payResponse.toMap());
        payBill.setRefNum(payResponse.getSender().getData().getExternalId());

        transaction.setRefNum(payResponse.getSender().getData().getExternalId());
        transaction.setStatus(ResponseStatus.BILLING_SUCCESS);
        var lastTransaction = transactionDAO.save(transaction);

        log.info("Loan: {} transaction is finished", requestDTO.getLoanExtId());

        temporaryService.successSaveToTemporary(
                lastTransaction,
                requestDTO.getDebitParams().getType(),
                requestDTO.getDebitParams().getProcessing(),
                requestDTO.getAddType()
        );
        return new ResultData<>(new PayResponseDTO(lastTransaction));
    }

    private ResultData<PayResponseDTO> saveFailTransaction(ResponseStatus responseStatus, Transaction transaction, String checkResponse) {
        transaction.setStatus(responseStatus);
        transaction.setNote(checkResponse);
        transactionDAO.save(transaction);
        var errorMessage = "Transaction finished with error: %s".formatted(transaction.getNote());
        log.info(errorMessage);
        return new ResultData<>(ResultState.NotProcessed, errorMessage);
    }


    private SimpleResponse<CheckRequest> checkRequestBuilder(PayRequestDTO requestDTO, Bill bill) {

        var loanBranch = requestDTO.getLoanExtId().substring(0, 5);

        var serviceIdResponse = getServiceIdForBranch(loanBranch);

        if (!serviceIdResponse.getStatus())
            return new SimpleResponse<>(397, serviceIdResponse.getError().getMessage());

        var serviceId = serviceIdResponse.getData();

        var ePosResponse = commonHelper.getEPosForBranch(requestDTO.getDebitParams().getProcessing(), loanBranch);

        if (!ePosResponse.getStatus()) {
            return new SimpleResponse<>(398, ePosResponse.getError().getMessage());
        }

        var ePos = ePosResponse.getData();

        var params = new HashMap<String, String>();
        params.put("schet", ePos.getAccount());

        var uuid = UUID.randomUUID();

        var preparedRequest = CheckRequest.builder()
                .agentTranId(uuid + "_" + bill.getTranId().getId())
                .account(requestDTO.getLoanExtId())
                .recipientId(serviceId)
                .amount(requestDTO.getAmount())
                .params(params)
                .build();

        log.info("CheckRequest:\n\n" + """
                        {
                            "recipientId": {},
                            "agentTranId": "{}",
                            "amount": {},
                            "account": "{}",
                            "params": {
                                "schet" : "{}"
                            }
                        }""",
                serviceId,
                preparedRequest.getAgentTranId(),
                preparedRequest.getAmount(),
                preparedRequest.getAccount(),
                ePos.getAccount()
        );

        bill.setRequestId(uuid);
        bill.setRequest(preparedRequest.toMap());
        bill.setCreditTo(ePos.getAccount());
        bill.setTerminalId(ePos.getTerminalId());
        bill.setMerchantId(ePos.getMerchantId());


        return new SimpleResponse<>(preparedRequest);
    }


    private SimpleResponse<String> getServiceIdForBranch(String loanBranch) {
        var branchOptional = Arrays.stream(Branch
                        .values())
                .filter(
                        b -> b.getCode().equals(loanBranch)
                )
                .findFirst();

        if (branchOptional.isEmpty())
            return new SimpleResponse<>(496, "Branch: %s not found!".formatted(loanBranch));


        var serviceId = billingProperty.getServices()
                .get(branchOptional.get());

        log.info("Service id: {} for branch: {}", serviceId, loanBranch);
        return new SimpleResponse<>(serviceId);
    }

    private SimpleResponse<PayRequest> payRequestBuilder(PayRequestDTO request, String billingId, Transaction transaction) {
        Map<String, String> sentParams = new HashMap<>();
        sentParams.putIfAbsent("paymentPurpose", "LOAN"); // This param need for UzCard Payment transaction.

        String card = request.getDebitParams().getCard();


        if (request.getDebitParams().getType().equals(CardType.pan)) {
            card = CardUtils.decryptPan(request.getDebitParams().getCard());

            if (request.getDebitParams().getProcessing().equals(Processing.UZCARD)) {
                if (!card.matches(CardUtils.regexUzCard()))
                    return new SimpleResponse<>(555, "UzCard processing type not matched for pan: %s".formatted(card));
            } else if (request.getDebitParams().getProcessing().equals(Processing.HUMO)) {
                if (!card.matches(CardUtils.regexHumo()))
                    return new SimpleResponse<>(555, "HuMo processing type not matched for pan: %s".formatted(card));
            } else
                return new SimpleResponse<>(556, "Processing: %s can't pay".formatted(request.getDebitParams().getProcessing()));

            sentParams.put(CardType.pan.toString(), card);
        } else if (request.getDebitParams().getType().equals(CardType.token)) {
            if (request.getDebitParams().getProcessing().equals(Processing.UZCARD)) {
                if (!card.matches(CardUtils.regexUzCardToken()))
                    return new SimpleResponse<>(555, "UzCard processing type not matched for token: %s".formatted(card));
            } else if (request.getDebitParams().getProcessing().equals(Processing.HUMO)) {
                if (!card.matches(CardUtils.regexHuMoToken()))
                    return new SimpleResponse<>(555, "HuMo processing type not matched for token: %s".formatted(card));
            } else
                return new SimpleResponse<>(556, "Processing: %s can't pay".formatted(request.getDebitParams().getProcessing()));

            sentParams.put(CardType.token.toString(), request.getDebitParams().getCard());
        }

        transaction.setAccount(card);


        var preparedRequest = new PayRequest(
                billingId,
                PayType.PC,
                sentParams
        );
        log.info("""
                        {
                            "billId": {},
                            "type": "{}",
                            "params": {
                                "{}": "{}",
                                "paymentPurpose": "LOAN"
                            }
                        }
                        """,
                billingId,
                preparedRequest.getType(),
                request.getDebitParams().getProcessing().equals(Processing.UZCARD) ? CardType.token : CardType.pan,
                request.getDebitParams().getCard());

        return new SimpleResponse<>(preparedRequest);
    }

    @Override
    public ResultData<PayResponseDTO> defaultReverseTransaction(@NonNull ReverseRequestDTO requestDTO) {
        log.info("Default reverse started by request: {}", requestDTO.getTranId());

        commonHelper.checkSwitcherIsEnabled();

        var transaction = getTransactionById(requestDTO.getTranId());

        reverseABSTransaction(transaction);

        log.info("ABS transaction is reversed. Now starting DEBIT reverse part");

        return reversePCPayment(transaction);
    }

    private Transaction getTransactionById(Long id) {
        var transaction = transactionDAO.findById(id);

        if (transaction.getStatus() != ResponseStatus.SUCCESS)
            throw new InvalidDataException("You can't reverse transactions with %s status".formatted(transaction.getStatus()));

        TranUtils.checkTranDate(transaction);

        return transaction;
    }


    private ResultData<PayResponseDTO> reversePCPayment(Transaction transaction) {
        log.info("Start reverse DEBIT transaction for tranId: {}", transaction.getId());

        var preparedDebitReverse = new CancelRequest(String.valueOf(transaction.getBills().getFirst().getBillingId()));


        var reverseBillOptional = BillUtils.getSuccessBill(transaction, TranType.CREDIT_REVERSE);


        if (reverseBillOptional.isEmpty()) {
            var message = "Bill not found with type CREDIT_REVERSE and status: SUCCESS";
            transaction.setNote(message);
            transaction.setStatus(ResponseStatus.S_CANCEL_FAILED);
            transactionDAO.save(transaction);
            return new ResultData<>(ResultState.NotProcessed, message);
        }

        var reverseBill = reverseBillOptional.get();

        var cancelBill = billBuilder(
                reverseBill,
                preparedDebitReverse.toMap(),
                transaction,
                TranType.DEBIT_CANCEL,
                ResponseStatus.PENDING
        );

        transaction.getBills().add(cancelBill);
        transaction.setStatus(ResponseStatus.CANCELLING);
        transactionDAO.save(transaction);

        var billingResponse = billingService.cancel(preparedDebitReverse);
        cancelBill.setResponse(billingResponse.toMap());
        cancelBill.setRefNum(transaction.getRefNum());

        if (billingResponse.getError() != null) {
            cancelBill.setStatus(ResponseStatus.NEED_CONFIRM);
            transaction.setNote(billingResponse.getError().getMessage());
            transaction.setStatus(ResponseStatus.S_NEED_CONFIRM);
            transactionDAO.save(transaction);
            return new ResultData<>(ResultState.NotProcessed, transaction.getNote());
        }

        if (billingResponse.getData().getStatus() != ResponseStatus.SENDER_CANCELLED) {
            cancelBill.setStatus(ResponseStatus.FAILED);
            transaction.setNote(billingResponse.getData().getResponseText());
            transaction.setStatus(ResponseStatus.CANCEL_FAILED);
            transactionDAO.save(transaction);
            return new ResultData<>(ResultState.NotProcessed, transaction.getNote());
        }


        cancelBill.setStatus(ResponseStatus.SUCCESS);
        transaction.setStatus(ResponseStatus.REVERSED);
        transactionDAO.save(transaction);

        return new ResultData<>(new PayResponseDTO(transaction));
    }


    private void reverseABSTransaction(Transaction transaction) {
        log.info("Start reverse CREDIT transaction for tranId: {}", transaction.getId());

        var actualEPosResult = commonHelper.getEPosForBranch(transaction.getProcessing(), transaction.getBranch().getCode());

        if (!actualEPosResult.getStatus())
            throw new InvalidDataException(actualEPosResult.getError().getMessage());

        var actualEPos = actualEPosResult.getData();

        var debitBill = BillUtils.getSuccessBill(transaction, TranType.DEBIT)
                .orElseThrow(() -> new EntityNotFoundException("Debit Bill not found in transaction: %d".formatted(transaction.getId())));

        if (!actualEPos.getMerchantId().equals(debitBill.getMerchantId())
                && !actualEPos.getTerminalId().equals(debitBill.getTerminalId()))
            throw new GeneralErrorException("Actual ePos: %s and Transaction ePos: %s don't matched".formatted(actualEPos.getTerminalId(), debitBill.getTerminalId()));


        if (transaction.getGeneralId() == null)
            throw new InvalidDataException("General id is null");

        var preparedCreditReverse = new NCIReverseDTO(transaction.getBranch().getCode(),
                transaction.getGeneralId());

        var reverseABSBill = billBuilder(
                debitBill,
                preparedCreditReverse.toMap(),
                transaction,
                TranType.CREDIT_REVERSE,
                ResponseStatus.PENDING
        );

        transaction.getBills().add(reverseABSBill);
        transaction.setStatus(ResponseStatus.REVERSING);
        transactionDAO.save(transaction);

        var reverseResponse = nciBillingService.reverseABSTransaction(preparedCreditReverse);
        reverseABSBill.setResponse(reverseResponse.toMap());

        if (reverseResponse.getError() != null) {
            reverseABSBill.setStatus(ResponseStatus.NEED_CONFIRM);

            transaction.setNote(reverseResponse.getError().getMessage());
            transaction.setStatus(ResponseStatus.R_NEED_CONFIRM);

            transactionDAO.save(transaction);
            throw new GeneralErrorException(transaction.getNote());
        }

        if (reverseResponse.getData().getCode() != 200) {
            reverseABSBill.setStatus(ResponseStatus.FAILED);

            transaction.setNote(reverseResponse.getData().getMessage());
            transaction.setStatus(ResponseStatus.REVERSE_FAILED);

            transactionDAO.save(transaction);
            throw new GeneralErrorException(transaction.getNote());
        }


        reverseABSBill.setRefNum(reverseResponse.getData().getGeneralId());
        reverseABSBill.setStatus(ResponseStatus.SUCCESS);
        transaction.setStatus(ResponseStatus.REVERSED);
        transactionDAO.save(transaction);
    }


    @Override
    public ResultData<PayResponseDTO> synchronize(Long id) {
        log.info("Starting synchronize transaction: {}", id);

        var transaction = transactionDAO.findById(id);

        if (!transaction.getStatus().equals(ResponseStatus.SUCCESS)
                && !transaction.getStatus().equals(ResponseStatus.SENDER_SUCCESS))
            throw new InvalidDataException("Please a wait finishing of transaction. Now: %s".formatted(transaction.getStatus()));

        var debitBill = BillUtils.getSuccessBill(transaction, TranType.DEBIT)
                .orElseThrow(() -> new EntityNotFoundException("Transaction #%d not found success debit bill".formatted(transaction.getId())));

        var debitCheckRequest = debitBill.getRequestId() + "_" + transaction.getId();
        var billingResponse = billingService.getPayment(debitCheckRequest);

        if (!billingResponse.getStatus())
            return new ResultData<>(ResultState.NotProcessed, billingResponse.getError().getMessage());

        var debitStatus = billingResponse.getData().getStatus();

        var debitSynchronizedBill = BillUtils
                .billBuilder(debitBill,
                        Map.of("pathVariable", debitCheckRequest),
                        transaction,
                        TranType.SYNCHRONIZE_PC,
                        debitStatus);

        debitSynchronizedBill.setResponse(billingResponse.toMap());
        debitSynchronizedBill.setMerchantId(billingResponse.getData().getEposMerchant());
        debitSynchronizedBill.setTerminalId(billingResponse.getData().getEposTerminal());
        debitSynchronizedBill.setDebitFrom(billingResponse.getData().getSenderAccount());
        debitSynchronizedBill.setCreditTo(billingResponse.getData().getRecipientAccount());

        transaction.getBills().add(debitSynchronizedBill);

        if (debitStatus.equals(ResponseStatus.SUCCESS)) {
            debitSynchronizedBill.setRefNum(billingResponse.getData().getSenderTrId());
            transaction.setStatus(ResponseStatus.SENDER_SUCCESS);
            transaction.setRefNum(billingResponse.getData().getSenderTrId());
            transactionDAO.save(transaction);
        } else {
            return saveFailTransaction(
                    ResponseStatus.FAILED,
                    transaction,
                    billingResponse.getData().getSenderStatusText()
            );
        }


        var creditBill = BillUtils.getSuccessBill(transaction, TranType.CREDIT)
                .orElseThrow(() -> new EntityNotFoundException("Transaction #%d not found success credit bill".formatted(transaction.getId())));


        var creditCheckRequest = billingResponse.getData().getSenderGateway().equals(Processing.HUMO.name()) ?
                billingResponse.getData().getHumoRefNumber() : billingResponse.getData().getSenderTrId();

        var absResponse = nciBillingService.checkPaymentFromABS(creditCheckRequest);

        if (!absResponse.getStatus())
            return new ResultData<>(ResultState.NotProcessed, absResponse.getError().getMessage());

        var creditStatus = absResponse.getData().getStatus().equals(ResponseStatus.SUCCESS) ? ResponseStatus.SUCCESS : ResponseStatus.FAILED;

        var creditSynchronizedBill = BillUtils
                .billBuilder(creditBill,
                        Map.of("requestParam", creditCheckRequest),
                        transaction,
                        TranType.SYNCHRONIZE_PC,
                        creditStatus);

        creditSynchronizedBill.setResponse(absResponse.toMap());

        transaction.getBills().add(creditSynchronizedBill);


        if (creditStatus.equals(ResponseStatus.SUCCESS)) {
            transaction.setStatus(ResponseStatus.SUCCESS);
            transaction.setGeneralId(absResponse.getData().getResult().getGeneralId());
            transactionDAO.save(transaction);
        } else {
            return saveFailTransaction(
                    ResponseStatus.FAILED,
                    transaction,
                    absResponse.getData().getError().getMessage()
            );
        }

        return ResultData.ok(new PayResponseDTO(transaction));
    }


    @Override
    public ResultData<PayResponseDTO> reverseCredit(@NonNull ReverseRequestDTO requestDTO) {
        log.info("Only credit reverse started by request: {}", requestDTO);
        commonHelper.checkSwitcherIsEnabled();

        var transaction = getTransactionById(requestDTO.getTranId());

        reverseABSTransaction(transaction);

        return new ResultData<>(new PayResponseDTO(transaction));
    }


    @Override
    public ResultData<PayResponseDTO> cancelDebit(@NonNull ReverseRequestDTO requestDTO) {
        log.info("Only debit reverse started by request: {}", requestDTO);

        commonHelper.checkSwitcherIsEnabled();

        var transaction = getTransactionById(requestDTO.getTranId());

        return reversePCPayment(transaction);
    }
}
