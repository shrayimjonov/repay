package uz.repay.payment.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.utils.BranchUtils;
import uz.repay.payment.model.dto.billing.request.CancelRequest;
import uz.repay.payment.model.dto.humo.response.HuMoBasePaymentResponse;
import uz.repay.payment.model.dto.nciBilling.response.NCIBaseResponse;
import uz.repay.payment.model.dto.nciBilling.response.NCICheckResponse;
import uz.repay.payment.model.entity.ABSSetting;
import uz.repay.payment.model.entity.Bill;
import uz.repay.payment.model.entity.Temporary;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.enums.TranType;
import uz.repay.payment.redis.entity.Tran;
import uz.repay.payment.service.business.*;
import uz.repay.payment.service.business.component.BankDayChecker;
import uz.repay.payment.service.core.TemporaryDAO;
import uz.repay.payment.service.core.TranRedisService;
import uz.repay.payment.service.core.TransactionDAO;
import uz.repay.payment.service.web.BillingService;
import uz.repay.payment.service.web.NCIBillingService;
import uz.repay.payment.utils.BillUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uz.repay.payment.utils.BillUtils.billBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TemporaryServiceImpl implements TemporaryService {
    private final TemporaryDAO temporaryDAO;
    private final NCIBillingService nciBillingService;
    private final TransactionDAO transactionDAO;
    private final BillingService billingService;
    private final ReverseService reverseService;
    private final BankDayChecker bankDayChecker;
    private final ABSService absService;
    private final PCMaintenanceService pcMaintenanceService;
    private final DebitTransService debitTransService;
    private final TranRedisService tranRedisService;

    @Override
    public void successSaveToTemporary(Transaction t,
                                       CardType cardType,
                                       Processing processing,
                                       AddType addType) {
        log.info("Transaction #{} save to temporary", t.getId());

        var debitBillOptional = t.getBills()
                .stream()
                .filter(z -> (z.getTranType().equals(TranType.DEBIT) || z.getTranType().equals(TranType.SYNCHRONIZE_PC)) &&
                        z.getStatus().equals(ResponseStatus.SUCCESS))
                .findFirst();

        if (debitBillOptional.isEmpty()) {
            log.error("Debit bill with SUCCESS status not found for transaction: {}", t.getId());
            return;
        }


        var temp = new Temporary(
                t.getLoanExtId(),
                t.getAmount(),
                cardType,
                t.getAccount(),
                processing,
                t.getCardPan(),
                t.getCardOwner(),
                t.getProcessing().equals(Processing.HUMO) ? debitBillOptional.get().getRefNum() : t.getRefNum(),
                t.getId(),
                addType
        );

        temporaryDAO.save(temp);
    }


    public void checkManualTransactionFromABS() {
        log.info("Starting check transactions from ABS");
        var manualTransactions = temporaryDAO.findAll();


        if (manualTransactions.isEmpty()) {
            log.info("Transactions is empty and will be exited");
            return;
        }

        for (var m : manualTransactions) {

            if (m.getProcessing().equals(Processing.DAVR_VISA)) {
                var bankDayResult = bankDayChecker.checkBankDay(m.getProcessing(), m.getLoanExtId());

                if (!bankDayResult) {
                    log.info("Visa transaction handling skipped, cause {} day is closed", BranchUtils.getBranchByLoan(m.getLoanExtId()));
                    continue;
                }
            }
            var transaction = transactionDAO.findById(m.getTranId());

            var debitOptional = transaction
                    .getBills()
                    .stream()
                    .filter(d -> d.getTranType().equals(TranType.DEBIT))
                    .filter(d -> d.getStatus().equals(ResponseStatus.SUCCESS))
                    .findFirst();

            if (debitOptional.isEmpty()) {
                log.error("Bill with tran type DEBIT not found");
                continue;
            }

            var debitBill = debitOptional.get();


            var absRequest = createFakeABSRequest(transaction);

            var creditBill = billBuilder(
                    debitOptional.get(),
                    absRequest,
                    transaction,
                    TranType.CREDIT,
                    ResponseStatus.PENDING
            );

            transaction.setStatus(ResponseStatus.CREDITING);
            transaction.getBills().add(creditBill);
            transactionDAO.save(transaction);

            var absResponse = nciBillingService.checkPaymentFromABS(m.getPaymentId());

            creditBill.setResponse(absResponse.toMap());

            if (handleCaughtErrors(m, absResponse, creditBill, transaction))
                continue;

            if (handleABSErrorAndCancel2Card(m, absResponse, creditBill, debitBill, transaction))
                continue;

            handleSuccessSituate(m, absResponse, creditBill, debitBill, transaction);
        }
    }

    public void checkManualTransactionFromABSV2() {
        log.info("Starting check transactions v2 from ABS");
        var manualTransactions = temporaryDAO.findAll();


        if (manualTransactions.isEmpty()) {
            log.info("Transactions is empty and will be exited");
            return;
        }

        manualTransactions
                .forEach(
                        m -> {
                            log.info("Begin handling temporary for loanExtId: {} with refNum: {}", m.getLoanExtId(), m.getPaymentId());

                            var redisTranOptional = tranRedisService.getTranById(m.getTranId());

                            if (redisTranOptional.isPresent()) {
                                log.warn("Transaction: {} for loan: {} already in handling!", m.getTranId(), m.getLoanExtId());
                                return;
                            }

                            var redisTranPrepared = new Tran(m.getTranId());
                            var redisTran = tranRedisService.save(redisTranPrepared);


                            var transaction = transactionDAO.findById(m.getTranId());

                            if (!transaction.getStatus().equals(ResponseStatus.SENDER_SUCCESS)
                                    && !transaction.getStatus().equals(ResponseStatus.RECIPIENT_SUCCESS)) {
                                log.info("Transaction status: {} and will be not handling", transaction.getStatus());
                                temporaryDAO.deleteById(m.getId());
                                tranRedisService.deleteById(redisTran.getId());
                                return;
                            }


                            var creditOptional = transaction
                                    .getBills()
                                    .stream()
                                    .filter(d -> d.getTranType().equals(TranType.CREDIT))
                                    .filter(d -> d.getStatus().equals(ResponseStatus.SUCCESS))
                                    .findFirst();

                            if (creditOptional.isEmpty()) {
                                log.error("Bill with tran type CREDIT not found");
                                return;
                            }

                            var debitBillOptional = transaction
                                    .getBills()
                                    .stream()
                                    .filter(d -> (d.getTranType().equals(TranType.DEBIT) || d.getTranType().equals(TranType.SYNCHRONIZE_PC)) && d.getStatus().equals(ResponseStatus.SUCCESS))
                                    .findFirst();

                            if (debitBillOptional.isEmpty()) {
                                log.error("Bill with tran type DEBIT not found");
                                return;
                            }


                            var creditBill = creditOptional.get();

                            transaction.setStatus(ResponseStatus.CHECKING);
                            transactionDAO.save(transaction);


                            var absResponse = nciBillingService.checkPaymentFromABS(transaction.getRefNum());

                            creditBill.setResponse(absResponse.toMap());

                            if (handleCaughtErrors(m, absResponse, creditBill, transaction)) {
                                tranRedisService.deleteById(redisTran.getId());
                                return;
                            }

                            if (handleABSErrorAndCancel2CardV2(m, absResponse, creditBill, transaction)) {
                                tranRedisService.deleteById(redisTran.getId());
                                return;
                            }

                            handleSuccessSituate(m, absResponse, creditBill, debitBillOptional.get(), transaction);
                            tranRedisService.deleteById(redisTran.getId());
                        }
                );
    }

    private void handleSuccessSituate(Temporary m, SimpleResponse<NCICheckResponse<NCIBaseResponse>> absResponse, Bill creditBill, Bill debitBill, Transaction transaction) {
        if (absResponse.getData().getResult().getCode() == 202) {
            log.info("Payment: {} Loan: {} is not finished yet...", m.getPaymentId(), m.getLoanExtId());
            transaction.setStatus(ResponseStatus.RECIPIENT_SUCCESS);
        } else {
            log.info("Payment: {} is finished and will be deleted from temporary", m.getPaymentId());

            creditBill.setStatus(ResponseStatus.SUCCESS);
            creditBill.setRefNum(absResponse.getData().getResult().getGeneralId());

            transaction.setGeneralId(absResponse.getData().getResult().getGeneralId());
            transaction.setStatus(ResponseStatus.DEBITING);
            transactionDAO.save(transaction);

            temporaryDAO.deleteById(m.getId());

            if (m.getProcessing().equals(Processing.HUMO)) {

                var debitConfirmBillOptional = BillUtils.getBillByTranType(transaction, TranType.DEBIT_CONFIRM);

                if (debitConfirmBillOptional.isEmpty()) {
                    log.info("Debit confirm bill not found and will be done for: {}", m.getPaymentId());
                    SimpleResponse<HuMoBasePaymentResponse> debitConfirmResult = debitTransService.huMoDebitConfirm(debitBill, transaction);

                    if (!debitConfirmResult.getStatus()) {
                        log.error("Debit confirm result is not success for tranId: {} and will be exited transaction handling!", transaction.getId());
                        return;
                    }
                } else {
                    log.info("Debit confirm bill already has! Continue method>>>");
                }

            }

            handleABSOtherTransactions(transaction);

            transaction.setStatus(ResponseStatus.SUCCESS);
        }
        transactionDAO.save(transaction);
    }


    private boolean handleABSErrorAndCancel2Card(Temporary m, SimpleResponse<NCICheckResponse<NCIBaseResponse>> absResponse, Bill creditBill, Bill debitBill, Transaction transaction) {
        if (absResponse.getData().getError() != null) {
            creditBill.setStatus(ResponseStatus.FAILED);
            log.info("Try to reverse transaction: {}", debitBill.getTranId().getId());

            var cancelRequest = new CancelRequest(String.valueOf(debitBill.getBillingId()));

            var cancelBill = billBuilder(
                    creditBill,
                    cancelRequest.toMap(),
                    transaction,
                    TranType.DEBIT_CANCEL,
                    ResponseStatus.PENDING
            );

            cancelBill.setRefNum(transaction.getRefNum());
            transaction.setStatus(ResponseStatus.REVERSING);
            transaction.getBills().add(cancelBill);
            transactionDAO.save(transaction);


            cancel2Card(m, transaction, cancelRequest, cancelBill);
            return true;
        }
        return false;
    }

    private boolean handleABSErrorAndCancel2CardV2(Temporary m, SimpleResponse<NCICheckResponse<NCIBaseResponse>> absResponse, Bill creditBill, Transaction transaction) {
        if (absResponse.getData().getError() != null) {

            creditBill.setStatus(ResponseStatus.FAILED);
            transaction.setNote(absResponse.getData().getError().getMessage());
            transaction.setStatus(ResponseStatus.FAILED);
            log.info("Try to reverse transaction: {}", transaction.getId());

            reverseService.cancelPC(
                    m.getPaymentId(),
                    creditBill.getMerchantId(),
                    creditBill.getTerminalId(),
                    creditBill.getDebitFrom(),
                    transaction
            );

            temporaryDAO.deleteById(m.getId());
            transactionDAO.save(transaction);

//            if (!CardUtils.isSumPC(transaction.getProcessing()))
//                reverseService.reverseForexOperation(transaction);

            return true;
        }
        return false;
    }


    private void cancel2Card(Temporary m, Transaction transaction, CancelRequest cancelRequest, Bill cancelBill) {
        var billingResponse = billingService.cancel(cancelRequest);

        if (billingResponse.getError() != null) {
            cancelBill.setStatus(ResponseStatus.NEED_CONFIRM);
            transaction.setNote(billingResponse.getError().getMessage());
            transaction.setStatus(ResponseStatus.S_NEED_CONFIRM);
            transactionDAO.save(transaction);
            temporaryDAO.deleteById(m.getId());
            return;
        }

        if (billingResponse.getData().getStatus() != ResponseStatus.SENDER_CANCELLED) {
            cancelBill.setStatus(ResponseStatus.FAILED);
            transaction.setNote(billingResponse.getData().getResponseText());
            transaction.setStatus(ResponseStatus.CANCEL_FAILED);
            transactionDAO.save(transaction);
            temporaryDAO.deleteById(m.getId());
            return;
        }

        cancelBill.setStatus(ResponseStatus.SUCCESS);
        transaction.setStatus(ResponseStatus.CANCELLED);
        transactionDAO.save(transaction);
    }

    private boolean handleCaughtErrors(Temporary m, SimpleResponse<NCICheckResponse<NCIBaseResponse>> absResponse, Bill creditBill, Transaction transaction) {
        if (absResponse.getError() != null) {
            creditBill.setStatus(ResponseStatus.FAILED);
            transaction.setNote(absResponse.getError().getMessage());

            if (absResponse.getError().getCode() == 580) {
                log.info("ABS loan payment doesn't answer and will be returned");
            } else {
                transaction.setStatus(ResponseStatus.R_NEED_CONFIRM);
                temporaryDAO.deleteById(m.getId());
            }
            transactionDAO.save(transaction);
            return true;
        }
        return false;
    }

    private Map<Object, Object> createFakeABSRequest(Transaction transaction) {
        return generateABSMockRequest(
                transaction.getRefNum(),
                transaction.getLoanExtId(),
                transaction.getCardPan(),
                String.valueOf(transaction.getBills().getFirst().getBillingId()),
                transaction.getBills().getFirst().getTerminalId(),
                transaction.getBills().getFirst().getMerchantId(),
                transaction.getBills().getFirst().getDebitFrom(),
                transaction.getAmount()
        );
    }


    private Map<Object, Object> generateABSMockRequest(String paymentId,
                                                       String loanId,
                                                       String cardNum,
                                                       String billingId,
                                                       String terminalId,
                                                       String merchantId,
                                                       String account,
                                                       Long amount

    ) {
        var request = new HashMap<>();
        var terminal = new HashMap<>();
        terminal.put("terminalId", terminalId);
        terminal.put("merchantId", merchantId);
        terminal.put("account", account);

        request.put("paymentId", paymentId);
        request.put("branch", loanId.substring(0, 5));
        request.put("formId", loanId.substring(5));
        request.put("cardNum", cardNum);
        request.put("refId", billingId);
        request.put("terminalId", terminal);
        request.put("amount", amount);
        request.put("originator", 3636); //3636 is PAYMO
        return request;
    }

    @Override
    public List<Temporary> findByLoanExtId(String loanExtId) {
        return temporaryDAO.findByLoanExtId(loanExtId);
    }

    private void handleABSOtherTransactions(Transaction transaction) {
        var pc = transaction.getProcessing();
        log.info("Check processing: {} to PC maintenance settings", pc);

        var absTransactionsOptional = pcMaintenanceService.findByProcessing(pc);

        if (absTransactionsOptional.isPresent()) {
            var absTransactions = absTransactionsOptional.get();

            if (absTransactions
                    .getAbsSettings()
                    .isEmpty()) {
                log.info("PC maintenance transactions is empty for PC: {} and will be returned", pc);
                return;
            }

            absTransactions.getAbsSettings()
                    .sort(Comparator.comparing(ABSSetting::getOrder));

            for (var setting : absTransactions.getAbsSettings()) {
                var result = absService.transaction(transaction, setting);

                if (!result.getStatus()) {
                    log.error("ABS transaction has error and will be exited with un success status");
                    break;
                }
            }


            log.info("PC maintenance settings transactions finished successfully");
        } else {
            log.info("PC maintenance settings not found for: {} and exited", pc);
        }
    }
}
