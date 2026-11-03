package uz.repay.payment.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.exception.CardException;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.utils.CardUtils;
import uz.repay.common.utils.DateUtils;
import uz.repay.payment.model.dto.absHub.request.AbsHubTransactionReverseRequest;
import uz.repay.payment.model.dto.absHub.response.AbsHubTransactionReverseResponse;
import uz.repay.payment.model.dto.humo.request.HuMoCancelPaymentByIdRequest;
import uz.repay.payment.model.dto.humo.request.HuMoGetPaymentByExtRequest;
import uz.repay.payment.model.dto.humo.request.HuMoReturnPaymentByIdRequest;
import uz.repay.payment.model.dto.humo.response.HuMoBaseEmptyResponse;
import uz.repay.payment.model.dto.master.request.MasterReverseByExtIdRequest;
import uz.repay.payment.model.dto.nciBilling.request.NCIReverseDTO;
import uz.repay.payment.model.dto.nciBilling.response.NCIBaseResponse;
import uz.repay.payment.model.dto.visa.request.VisaTranBaseRequest;
import uz.repay.payment.model.entity.Bill;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.enums.ABSPayType;
import uz.repay.payment.model.enums.HuMoStatus;
import uz.repay.payment.model.enums.TranType;
import uz.repay.payment.model.enums.UzCardStatus;
import uz.repay.payment.model.property.HumoGatewayProperty;
import uz.repay.payment.model.property.InternalProperty;
import uz.repay.payment.service.business.ABSService;
import uz.repay.payment.service.business.DebitTransService;
import uz.repay.payment.service.business.PCMaintenanceService;
import uz.repay.payment.service.business.ReverseService;
import uz.repay.payment.service.business.component.BankDayChecker;
import uz.repay.payment.service.core.TransactionDAO;
import uz.repay.payment.service.web.*;
import uz.repay.payment.utils.BillUtils;

import java.time.LocalDate;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReverseServiceImpl implements ReverseService {
    private final UzCardIntegrationService uzCardIntegrationService;
    private final TransactionDAO transactionDAO;
    private final HuMoGatewayService huMoGatewayService;
    private final HumoGatewayProperty humoGatewayProperty;
    private final NCIBillingService nciBillingService;
    private final InternalProperty internalProperty;
    private final VisaIntegrationService visaIntegrationService;
    private final ABSService absService;
    private final PCMaintenanceService pcMaintenanceService;
    private final BankDayChecker bankDayChecker;
    private final AbsHubClientService absHubClientService;
    private final DebitTransService debitTransService;
    private final MasterGatewayClientService masterGatewayClientService;

    @Override
    public void cancelPC(
            String utrnno,
            String merchantId,
            String terminalId,
            String account,
            Transaction transaction) {

        log.info("Start PC cancel method for reversing rrn: {}", utrnno);

        switch (transaction.getProcessing()) {
            case UZCARD, SVGATE_AUTOPAY -> cancelToUzCard(utrnno, merchantId, terminalId, account, transaction);
            case HUMO -> cancelToHuMo(utrnno, merchantId, terminalId, account, transaction);
            case DAVR_VISA -> cancelToVisa(account, merchantId, terminalId, transaction);
            case DAVR_MASTER_CARD -> cancelToMaster(utrnno, merchantId, terminalId, account, transaction);
            default ->
                    throw new CardException("Cancel method not detected for processing: %s".formatted(transaction.getProcessing()));
        }
    }

    public void cancelToUzCard(String utrnno,
                               String merchantId,
                               String terminalId,
                               String account,
                               Transaction transaction
    ) {
        var reverseBill = new Bill(
                transaction.getAmount(),
                account,
                transaction.getCardPan(),
                utrnno,
                merchantId,
                terminalId,
                TranType.DEBIT_CANCEL,
                ResponseStatus.CANCELLING,
                Map.of("requestParam:reverseExtSv", utrnno),
                transaction,
                Currency.UZS
        );
        transaction.getBills().add(reverseBill);

        var uzCardCancelResponse = uzCardIntegrationService.reverse(utrnno);

        reverseBill.setResponse(uzCardCancelResponse.toMap());

        if (!uzCardCancelResponse.getStatus()) {
            handleFailedStatus(transaction, ResponseStatus.NEED_CONFIRM, reverseBill, uzCardCancelResponse.getError().getMessage());
            return;
        }

        if (uzCardCancelResponse.getData().getError() != null) {
            handleFailedStatus(
                    transaction, ResponseStatus.CANCEL_FAILED, reverseBill, uzCardCancelResponse.getData().getError().getMessage()
            );
            return;
        }

        var cancelSuccessOptional = uzCardCancelResponse.getData().getResult()
                .stream()
                .filter(z -> z.getStatus().equals(UzCardStatus.ROK))
                .findFirst();

        if (cancelSuccessOptional.isEmpty()) {
            handleFailedStatus(
                    transaction, ResponseStatus.CANCEL_FAILED, reverseBill, "Cancel is failed"
            );
            return;
        }

        reverseBill.setStatus(ResponseStatus.SUCCESS);
        transaction.setStatus(ResponseStatus.CANCELLED);
        transactionDAO.save(transaction);
    }

    public void cancelToHuMo(String utrnno,
                             String merchantId,
                             String terminalId,
                             String account,
                             Transaction transaction
    ) {


        var cancelActiveRequest = cancelRequestBuilder(transaction);

        var preparedReverseBill = new Bill(
                transaction.getAmount(),
                account,
                transaction.getCardPan(),
                utrnno,
                merchantId,
                terminalId,
                TranType.DEBIT_CANCEL,
                ResponseStatus.CANCELLING,
                cancelActiveRequest.toMap(),
                transaction,
                Currency.UZS
        );
        transaction.getBills().add(preparedReverseBill);
        var reverseBillOptional = BillUtils.getBillByStatus(transaction, TranType.DEBIT_CANCEL, ResponseStatus.CANCELLING);

        if (reverseBillOptional.isEmpty()) {
            log.error("Reverse bill with tranType: {} and with status: {} not found", TranType.DEBIT_CANCEL, ResponseStatus.CANCELLING);
            throw new InvalidDataException("Reverse bill not found for transaction: %d".formatted(transaction.getId()));
        }

        var reverseBill = reverseBillOptional.get();

        var getPaymentByExtResponse = huMoGatewayService.getPaymentByExt(
                new HuMoGetPaymentByExtRequest(
                        humoGatewayProperty.getOriginator(),
                        generateTransactionId(transaction),
                        BillUtils.HUMO_GATE_SUBSYSTEM
                )
        );

        reverseBill.setResponse(getPaymentByExtResponse.toMap());

        if (!getPaymentByExtResponse.getStatus()) {
            handleFailedStatus(transaction, ResponseStatus.NEED_CONFIRM, reverseBill, getPaymentByExtResponse.getError().getMessage());
            return;
        }


        if (getPaymentByExtResponse.getData().getError() != null) {
            handleFailedStatus(transaction, ResponseStatus.FAILED, reverseBill, getPaymentByExtResponse.getData().getError().getMessage());
            return;
        }

        if (transaction.getCreatedDate().isBefore(DateUtils.getDateNow().minusDays(10))) {
            log.info("Transaction: {} is older than 10 days", transaction.getRefNum());

            if (getPaymentByExtResponse.getData().getStatus().equals(HuMoStatus.ACTIVE.name().toLowerCase())) {
                log.info("Transaction: {} is older and must confirm and reverse", transaction.getRefNum());
                var debitBill = BillUtils.takeSuccessBill(transaction, TranType.DEBIT);
                var debitResult = debitTransService.huMoDebitConfirm(
                        debitBill,
                        transaction
                );

                log.info("Debit result is: {}", debitResult);

                if (!debitResult.getStatus())
                    throw new GeneralErrorException(debitResult.getError().getMessage());
            }

            getPaymentByExtResponse.getData().setStatus(HuMoStatus.FINISHED.name().toLowerCase());
        }

        SimpleResponse<HuMoBaseEmptyResponse> huMoCancelResponse;

        if (getPaymentByExtResponse.getData().getStatus().equals(HuMoStatus.ACTIVE.name().toLowerCase()))
            huMoCancelResponse = huMoGatewayService.cancelById(cancelActiveRequest);
        else if (getPaymentByExtResponse.getData().getStatus().equals(HuMoStatus.FINISHED.name().toLowerCase())) {
            var returnRequest = returnBillBuilder(transaction);

            reverseBill.setRequest(returnRequest.toMap());
            huMoCancelResponse = huMoGatewayService.returnById(returnRequest);
        } else {

            if (getPaymentByExtResponse.getData().getStatus().equals(HuMoStatus.RETURNED.name().toLowerCase()))
                transaction.setStatus(ResponseStatus.REVERSED);
            else
                transaction.setStatus(ResponseStatus.CANCELLED);

            transactionDAO.save(transaction);
            return;
        }

        log.info("HuMo cancel response: {}", huMoCancelResponse);

        reverseBill.setResponse(huMoCancelResponse.toMap());

        if (!huMoCancelResponse.getStatus()) {
            handleFailedStatus(transaction, ResponseStatus.NEED_CONFIRM, reverseBill, huMoCancelResponse.getError().getMessage());
            return;
        }

        if (huMoCancelResponse.getData().getError() != null) {
            handleFailedStatus(
                    transaction, ResponseStatus.CANCEL_FAILED, reverseBill, huMoCancelResponse.getData().getError().getMessage()
            );
            return;
        }

        reverseBill.setStatus(ResponseStatus.SUCCESS);
        transaction.setStatus(ResponseStatus.CANCELLED);
        transactionDAO.save(transaction);
    }

    private HuMoReturnPaymentByIdRequest returnBillBuilder(Transaction transaction) {
        var debitBill = transaction.getBills()
                .stream()
                .filter(z -> z.getTranType().equals(TranType.DEBIT)
                        && z.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException("Success DEBIT bill not found for cancel payment"));

        log.info("Found debit bill for reversing: {}", debitBill);

        return new HuMoReturnPaymentByIdRequest(
                humoGatewayProperty.getOriginator(),
                debitBill.getRefNum(),
                debitBill.getMerchantId(),
                debitBill.getTerminalId(),
                BillUtils.HUMO_GATE_SUBSYSTEM
        );
    }

    private HuMoCancelPaymentByIdRequest cancelRequestBuilder(Transaction transaction) {
        var debitBill = transaction.getBills()
                .stream()
                .filter(z -> z.getTranType().equals(TranType.DEBIT)
                        && z.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException("Success DEBIT bill not found for cancel payment"));

        log.info("Found debit bill for reversing: {}", debitBill);

        var cancelActiveRequest = new HuMoCancelPaymentByIdRequest(
                humoGatewayProperty.getOriginator(),
                debitBill.getRefNum(),
                BillUtils.HUMO_GATE_SUBSYSTEM
        );

        log.info("Prepared cancel active request: {}", cancelActiveRequest);
        return cancelActiveRequest;
    }

    private void handleFailedStatus(Transaction transaction,
                                    ResponseStatus status,
                                    Bill bill, String message) {
        log.info("Entered to failed method, tranId: {}", transaction.getId());
        transaction.setNote(message);
        transaction.setStatus(status);
        bill.setStatus(ResponseStatus.FAILED);
        transactionDAO.save(transaction);
        var errorMessage = "Transaction finished with error: %s".formatted(transaction.getNote());
        log.info(errorMessage);
    }

    @Override
    @Deprecated
    public void reverseABS(Transaction transaction, String merchantId, String terminalId, String creditTo) {
        log.info("Begin debit reverse step for tranId: {}", transaction.getId());
        var preparedCreditReverse = new NCIReverseDTO(transaction.getBranch().getCode(),
                transaction.getGeneralId());

        var bankDayResponse = bankDayChecker.checkBankDay(transaction.getProcessing(), transaction.getLoanExtId());

        if (!bankDayResponse)
            throw new GeneralErrorException("Bank day for: %s is closed".formatted(transaction.getBranch().getCode()));

        var reverseABSBill = new Bill(
                transaction.getAmount(),
                transaction.getLoanExtId(),
                creditTo,
                null,
                merchantId,
                terminalId,
                TranType.CREDIT_REVERSE,
                ResponseStatus.CANCELLING,
                preparedCreditReverse.toMap(),
                transaction,
                Currency.UZS
        );

        transaction.getBills().add(reverseABSBill);
        transaction.setStatus(ResponseStatus.REVERSING);


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


        reverseABSBill.setRefNum(reverseResponse.getData().getParams().getOrDefault("doc_id", "NOT_FOUND"));
        reverseABSBill.setStatus(ResponseStatus.SUCCESS);


        boolean isSumProcessing = CardUtils.isSumPC(transaction.getProcessing());

        var status = isSumProcessing ?
                ResponseStatus.REVERSED : ResponseStatus.REVERSING;

        transaction.setStatus(status);
        transactionDAO.save(transaction);

        if (!isSumProcessing) {
            reverseTransitOperation(transaction);

            reverseForexOperation(transaction);

        }

    }


    private String generateTransactionId(Transaction transaction) {
        return internalProperty.getPayment().getExternalPrefix().formatted(transaction.getId());
    }


    public void cancelToVisa(
            String account,
            String merchantId,
            String terminalId,
            Transaction transaction
    ) {

        var cancelVisaRequest = new VisaTranBaseRequest(
                transaction.getAccount(),
                transaction.getAmount()
        );

        log.info("Prepared visa cancel request: {}", cancelVisaRequest);

        var reverseBill = new Bill(
                transaction.getAmount(),
                account,
                transaction.getCardPan(),
                null,
                merchantId,
                terminalId,
                TranType.DEBIT_CANCEL,
                ResponseStatus.CANCELLING,
                cancelVisaRequest.toMap(),
                transaction,
                Currency.USD
        );
        transaction.getBills().add(reverseBill);


        var visaCancelResponse = visaIntegrationService.reverse(cancelVisaRequest);


        log.info("Visa cancel response: {}", visaCancelResponse);

        reverseBill.setResponse(visaCancelResponse.toMap());

        if (!visaCancelResponse.getStatus()) {
            handleFailedStatus(transaction, ResponseStatus.NEED_CONFIRM, reverseBill, visaCancelResponse.getError().getMessage());
            return;
        }

        if (visaCancelResponse.getData().getError() != null) {
            handleFailedStatus(
                    transaction, ResponseStatus.CANCEL_FAILED, reverseBill, visaCancelResponse.getData().getError().getMessage()
            );
            return;
        }

        reverseBill.setRefNum(visaCancelResponse.getData().getResult().getInternalNo());
        reverseBill.setStatus(ResponseStatus.SUCCESS);
        transaction.setStatus(ResponseStatus.CANCELLED);
        transactionDAO.save(transaction);
    }


    @Override
    public void reverseForexOperation(Transaction transaction) {
        // reverse forex Transaction
        var pcMaintenanceOptional = pcMaintenanceService
                .findByProcessing(Processing.DAVR_VISA);

        if (pcMaintenanceOptional.isEmpty()) {
            log.error("PC maintenance not found for pc: {}", transaction.getProcessing());
            return;
        }

        var forexReverseOperationOptional =
                pcMaintenanceOptional
                        .get()
                        .getAbsSettings()
                        .stream()
                        .filter(z -> z.getPayType().equals(ABSPayType.FOREX_RESIDENT))
                        .findFirst();

        if (forexReverseOperationOptional.isEmpty()) {
            log.error("PC maintenance FOREX operation not found for pc: {}", transaction.getProcessing());
            return;
        }


        var response = absService.forexReverseTransaction(transaction, forexReverseOperationOptional.get());

        log.info("ABS reverse forex operation result: {}", response);

        if (!response.getStatus()) {
            throw new GeneralErrorException(response.getError().getMessage());
        }
    }

    private void reverseTransitOperation(Transaction transaction) {
        var transitTransactionOptional =
                transaction
                        .getBills()
                        .stream()
                        .filter(z -> z.getTranType().equals(TranType.TRANSIT) && z.getStatus().equals(ResponseStatus.SUCCESS))
                        .findFirst();

        if (transitTransactionOptional.isEmpty()) {
            log.info("Transaction: {} transit bill not found and will be exited", transaction.getId());
            return;
        }

        var transitTransaction = transitTransactionOptional.get();

        // reverse transit Transaction
        var pcMaintenanceOptional = pcMaintenanceService
                .findByProcessing(Processing.DAVR_VISA);

        if (pcMaintenanceOptional.isEmpty()) {
            log.error("PC maintenance not found for pc: {}", transaction.getProcessing());
            return;
        }


        var transitOperationOptional =
                pcMaintenanceOptional
                        .get()
                        .getAbsSettings()
                        .stream()
                        .filter(z -> z.getPayType().equals(ABSPayType.SIMPLE_PAY))
                        .findFirst();

        if (transitOperationOptional.isEmpty()) {
            log.error("PC maintenance Simple Pay operation not found for pc: {}", transaction.getProcessing());
            return;
        }

        var absSetting = transitOperationOptional.get();

        log.info("Found abs setting: {}", absSetting.getId());


        var request =
                new AbsHubTransactionReverseRequest(
                        transaction.getBranch().getCode(),
                        transitTransaction.getRefNum()
                );

        log.info("ABS transit reverse prepared request: {}", request);


        var transitBill = new Bill(
                transaction.getAmount(),
                absSetting.getAccountDebit(),
                absSetting.getAccountCredit(),
                null,
                null,
                transaction,
                ResponseStatus.PENDING,
                TranType.TRANSIT_REVERSE,
                request.toMap(),
                Currency.UZS
        );

        transaction.getBills().add(transitBill);

        var response = absHubClientService.reverse(request);
        log.info("ABS reverse simple pay operation result: {}", response);

        transitBill.setResponse(response.toMap());

        var reverseResult = handleReverseResponseV2(transaction, response, transitBill);

        log.info("Finished reverse transit transaction with: {}", reverseResult);

        if (!response.getIsSuccess()) {
            throw new GeneralErrorException(response.getError().getMessage());
        }
    }

    private SimpleResponse<NCIBaseResponse> handleReverseResponse(Transaction transaction, SimpleResponse<NCIBaseResponse> response, Bill transitBill) {
        if (!response.getStatus()) {
            transitBill.setStatus(ResponseStatus.FAILED);
            transaction.setNote(response.getError().getMessage());
            transaction.setStatus(ResponseStatus.NEED_CONFIRM);
            transactionDAO.save(transaction);
            return new SimpleResponse<>(
                    637,
                    response.getError().getMessage()
            );
        }


        switch (response.getData().getCode()) {
            case 447 -> {
                transitBill.setStatus(ResponseStatus.FAILED);
                transaction.setNote(response.getData().getMessage());
                transaction.setStatus(ResponseStatus.NEED_CONFIRM);
                transactionDAO.save(transaction);
                return new SimpleResponse<>(
                        637,
                        response.getData().getMessage()
                );
            }
            case 200 -> {
                transitBill.setStatus(ResponseStatus.SUCCESS);
                transitBill.setRefNum(response.getData().getGeneralId());
                var status = ResponseStatus.REVERSED;
                transaction.setStatus(status);
                transactionDAO.save(transaction);
                return new SimpleResponse<>(response.getData());
            }
            default -> {
                transitBill.setStatus(ResponseStatus.FAILED);
                transaction.setNote(response.getData().getMessage());
                transaction.setStatus(ResponseStatus.FAILED);
                transactionDAO.save(transaction);
                return new SimpleResponse<>(
                        638,
                        response.getData().getMessage()
                );
            }
        }
    }


    @Override
    public void reverseOnlyOneABSPaymentV2(Transaction transaction, String creditTo) {
        log.info("Begin debit reverse v2 for tranId: {}", transaction.getId());

        var bankDayResponse = bankDayChecker.checkBankDay(transaction.getProcessing(), transaction.getLoanExtId());

        if (!bankDayResponse)
            throw new GeneralErrorException("Bank day for: %s is closed".formatted(transaction.getBranch().getCode()));

        var preparedCreditReverse = new AbsHubTransactionReverseRequest(transaction.getBranch().getCode(),
                transaction.getGeneralId());


        var preapredReverseABSBill = new Bill(
                transaction.getAmount(),
                transaction.getLoanExtId(),
                creditTo,
                null,
                null,
                null,
                TranType.CREDIT_REVERSE,
                ResponseStatus.REVERSING,
                preparedCreditReverse.toMap(),
                transaction,
                Currency.UZS
        );

        transaction.getBills().add(preapredReverseABSBill);
        transaction.setStatus(ResponseStatus.REVERSING);

        transactionDAO.save(transaction);

        var reverseABSBillOptional = BillUtils.getBillByStatus(transaction, TranType.CREDIT_REVERSE, ResponseStatus.REVERSING);

        if (reverseABSBillOptional.isEmpty())
            throw new EntityNotFoundException("Credit reverse bill with status reversing not found in tranId: %d".formatted(transaction.getId()));

        var reverseABSBill = reverseABSBillOptional.get();

        var reverseResponse = absHubClientService.reverse(preparedCreditReverse);
        reverseABSBill.setResponse(reverseResponse.toMap());

        if (reverseResponse.getError() != null) {
            reverseABSBill.setStatus(ResponseStatus.FAILED);

            if (reverseResponse.getError().getCode().equals(580)) {
                transaction.setStatus(ResponseStatus.R_NEED_CONFIRM);
            } else {
                transaction.setStatus(ResponseStatus.FAILED);
            }

            transaction.setNote(reverseResponse.getError().getMessage());
            transactionDAO.save(transaction);
            throw new GeneralErrorException(transaction.getNote());
        }

        reverseABSBill.setRefNum(reverseResponse.getResult().getGeneralId());
        reverseABSBill.setStatus(ResponseStatus.SUCCESS);

        transaction.setStatus(ResponseStatus.REVERSED);
        transactionDAO.save(transaction);

    }

    private SimpleResponse<AbsHubTransactionReverseResponse> handleReverseResponseV2(Transaction transaction, ResultData<AbsHubTransactionReverseResponse> response, Bill transitBill) {
        if (!response.getIsSuccess()) {
            transitBill.setStatus(ResponseStatus.FAILED);
            transaction.setNote(response.getError().getMessage());

            if (response.getError().getCode().equals(580))
                transaction.setStatus(ResponseStatus.NEED_CONFIRM);
            else
                transaction.setStatus(ResponseStatus.FAILED);

            transactionDAO.save(transaction);
            return new SimpleResponse<>(
                    637,
                    response.getError().getMessage()
            );
        }

        transitBill.setStatus(ResponseStatus.SUCCESS);
        transitBill.setRefNum(response.getResult().getGeneralId());
        var status = ResponseStatus.REVERSED;
        transaction.setStatus(status);
        transactionDAO.save(transaction);
        return new SimpleResponse<>(response.getResult());
    }

    @Override
    public void reverseTransitAndForex(Transaction transaction) {

        var transitReverseResult = transaction
                .getBills()
                .stream()
                .filter(bill -> bill.getTranType().equals(TranType.TRANSIT_REVERSE)
                        && bill.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst();
        log.info("Transit reverse bill isPresent: {}", transitReverseResult.isPresent());

        if (transitReverseResult.isEmpty())
            reverseTransitOperation(transaction);

        var forexReverseResult = transaction
                .getBills()
                .stream()
                .filter(bill -> bill.getTranType().equals(TranType.FOREX_REVERSE)
                        && bill.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst();
        log.info("Forex reverse bill isPresent: {}", transitReverseResult.isPresent());

        if (forexReverseResult.isEmpty())
            reverseForexOperation(transaction);
    }

    public void cancelToMaster(String utrnno,
                               String merchantId,
                               String terminalId,
                               String account,
                               Transaction transaction
    ) {


        var cancelActiveRequest = cancelMasterRequestBuilder(transaction);

        var reverseBill = new Bill(
                transaction.getAmount(),
                account,
                transaction.getCardPan(),
                utrnno,
                merchantId,
                terminalId,
                TranType.DEBIT_CANCEL,
                ResponseStatus.CANCELLING,
                cancelActiveRequest.toMap(),
                transaction,
                Currency.UZS
        );
        transaction.getBills().add(reverseBill);


        var masterCancelResponse = masterGatewayClientService.reverseByExtId(cancelActiveRequest);


        reverseBill.setResponse(masterCancelResponse.toMap());

        if (!masterCancelResponse.getStatus()) {

            if (masterCancelResponse.getError().getCode().equals(580)) {
                handleFailedStatus(transaction, ResponseStatus.NEED_CONFIRM, reverseBill, masterCancelResponse.getError().getMessage());
            } else {
                handleFailedStatus(transaction, ResponseStatus.FAILED, reverseBill, masterCancelResponse.getError().getMessage());
            }
            return;
        }

        if (masterCancelResponse.getData().getError() != null) {
            handleFailedStatus(
                    transaction, ResponseStatus.CANCEL_FAILED, reverseBill, masterCancelResponse.getData().getError().getMessage()
            );
            return;
        }

        reverseBill.setStatus(ResponseStatus.SUCCESS);
        transaction.setStatus(ResponseStatus.CANCELLED);
        transactionDAO.save(transaction);
    }

    private MasterReverseByExtIdRequest cancelMasterRequestBuilder(Transaction transaction) {
        var debitBill = transaction.getBills()
                .stream()
                .filter(z -> z.getTranType().equals(TranType.DEBIT)
                        && z.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException("Success DEBIT bill not found for cancel payment"));

        log.info("Found debit bill for reversing: {}", debitBill);

        var cancelActiveRequest = new MasterReverseByExtIdRequest(
                generateTransactionId(transaction)
        );

        log.info("Prepared cancel active request: {}", cancelActiveRequest);
        return cancelActiveRequest;
    }
}
