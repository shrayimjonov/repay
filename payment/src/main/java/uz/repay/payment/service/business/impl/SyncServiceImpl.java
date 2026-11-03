package uz.repay.payment.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.exception.CardException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.DateUtils;
import uz.repay.payment.model.dto.core.pay.response.result.SyncResult;
import uz.repay.payment.model.dto.humo.request.HuMoGetPaymentByExtRequest;
import uz.repay.payment.model.dto.master.request.MasterSyncByExtIdRequest;
import uz.repay.payment.model.dto.uzcard.response.result.UzCardBasePaymentResult;
import uz.repay.payment.model.dto.visa.request.VisaHistoryRequest;
import uz.repay.payment.model.dto.visa.response.result.VisaHistoryResult;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.enums.HuMoStatus;
import uz.repay.payment.model.enums.TranType;
import uz.repay.payment.model.enums.UzCardStatus;
import uz.repay.payment.model.property.HumoGatewayProperty;
import uz.repay.payment.service.business.SyncService;
import uz.repay.payment.service.web.*;
import uz.repay.payment.utils.BillUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static uz.repay.payment.utils.BillUtils.VISA_SUB_SYSTEM;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncServiceImpl implements SyncService {
    private final UzCardIntegrationService uzCardIntegrationService;
    private final HuMoGatewayService huMoGatewayService;
    private final HumoGatewayProperty humoGatewayProperty;
    private final NCIBillingService nciBillingService;
    private final VisaIntegrationService visaIntegrationService;
    private final MasterGatewayClientService masterGatewayClientService;

    @Override
    public SyncResult syncPC(String extId, Transaction transaction) {
        log.info("Starting sync PC transaction with extId: {}", extId);
        var pc = transaction.getProcessing();

        switch (pc) {
            case Processing.UZCARD, Processing.SVGATE_AUTOPAY -> {
                var sr = syncUzCard(extId);
                log.info("Return UZCARD sync result: {}", sr);
                return sr;

            }
            case Processing.HUMO -> {
                var sr = syncHuMo(extId);
                log.info("Return HUMO sync result: {}", sr);
                return sr;
            }
            case DAVR_VISA -> {
                var sr = syncDavrVISA(transaction);
                log.info("Return DAVR VISA sync result: {}", sr);
                return sr;
            }
            case DAVR_MASTER_CARD -> {
                var sr = syncMaster(extId);
                log.info("Return Master sync result: {}", sr);
                return sr;
            }
            default -> throw new CardException("Cancel method not detected for processing: %s".formatted(pc));
        }
    }

    @Override
    public SyncResult syncUzCard(String extId) {
        var syncResult = new SyncResult();
        syncResult.setRequest(Map.of("requestParam:reverseExtSv", extId));

        var uzCardResponse = uzCardIntegrationService.getPaymentByRefNum(extId);
        syncResult.setResponse(uzCardResponse.toMap());

        if (!uzCardResponse.getStatus()) {
            log.error("UzCard sync has error: {}", uzCardResponse.getError());
            syncResult.setError(uzCardResponse.getError());
            syncResult.setStatus(ResponseStatus.NEED_CONFIRM);
            return syncResult;
        }

        if (uzCardResponse.getData().getError() != null) {
            log.error("UzCard sync response has error: {}", uzCardResponse.getData().getError());
            syncResult.setError(uzCardResponse.getData().getError());
            syncResult.setStatus(ResponseStatus.FAILED);
            return syncResult;
        }


        var resultEnumOptional = uzCardResponse
                .getData()
                .getResult()
                .stream()
                .map(UzCardBasePaymentResult::getStatus)
                .max(Comparator.naturalOrder());


        if (resultEnumOptional.isEmpty()) {
            log.error("Response status enum not found");
            syncResult.setError(new CoreError(2002, "Response status enum not found"));
            syncResult.setStatus(ResponseStatus.FAILED);
            return syncResult;
        }

        var resultEnum = resultEnumOptional.get();


        log.info("Priority payment status: {} ", resultEnum);

        switch (resultEnum) {
            case ERR -> {
                var payment = getPaymentByStatus(uzCardResponse
                        .getData()
                        .getResult(), UzCardStatus.ERR);
                var message = "Payment: %s failed: %s".formatted(extId, payment.getResp() + " " + payment.getRespText());
                log.error(message);
                syncResult.setError(new CoreError(Integer.valueOf(payment.getRespSV()), message));
                syncResult.setStatus(ResponseStatus.FAILED);
                return syncResult;
            }
            case RER -> {
                var payment = getPaymentByStatus(uzCardResponse
                        .getData()
                        .getResult(), UzCardStatus.RER);
                var message = "Payment reverse: %s failed: %s".formatted(extId, payment.getResp() + " " + payment.getRespText());
                log.error(message);
                syncResult.setRRN(payment.getRefNum());
                syncResult.setError(new CoreError(Integer.valueOf(payment.getRespSV()), message));
                syncResult.setStatus(ResponseStatus.REVERSE_FAILED);
                return syncResult;
            }
            case ROK -> {
                var payment = getPaymentByStatus(uzCardResponse
                        .getData()
                        .getResult(), UzCardStatus.ROK);
                var message = "Payment reverse: %s success".formatted(extId);
                log.info(message);
                syncResult.setRRN(payment.getRefNum());
                syncResult.setStatus(ResponseStatus.REVERSED);
                syncResult.setError(new CoreError(2001, message));
                return syncResult;
            }
            case OK -> {
                var payment = getPaymentByStatus(uzCardResponse
                        .getData()
                        .getResult(), UzCardStatus.OK);
                var message = "Payment: %s success".formatted(extId);
                log.info(message);
                syncResult.setIsSuccess(true);
                syncResult.setRRN(payment.getRefNum());
                syncResult.setStatus(ResponseStatus.SENDER_SUCCESS);
                return syncResult;
            }
            default -> throw new InvalidDataException("%s enum for extId: %s handling not found. Contact with coder"
                    .formatted(resultEnum, extId));
        }


    }

    @SuppressWarnings("all")
    public UzCardBasePaymentResult getPaymentByStatus(List<UzCardBasePaymentResult> result, UzCardStatus status) {
        return
                result
                        .stream()
                        .filter(z -> z.getStatus().equals(status))
                        .findFirst()
                        .get();
    }

    @Override
    public SyncResult syncHuMo(String extId) {

        var syncResult = new SyncResult();

        var request = new HuMoGetPaymentByExtRequest(
                humoGatewayProperty.getOriginator(),
                extId,
                BillUtils.HUMO_GATE_SUBSYSTEM
        );
        syncResult.setRequest(request.toMap());

        var huMoResponse = huMoGatewayService.getPaymentByExt(
                request
        );

        syncResult.setResponse(huMoResponse.toMap());


        if (!huMoResponse.getStatus()) {
            log.error("HuMo sync has error: {}", huMoResponse.getError());
            syncResult.setError(huMoResponse.getError());
            syncResult.setStatus(ResponseStatus.NEED_CONFIRM);
            return syncResult;
        }

        if (huMoResponse.getData().getError() != null) {
            log.error("HuMo sync response has error: {}", huMoResponse.getData().getError());
            syncResult.setError(huMoResponse.getData().getError());
            syncResult.setStatus(ResponseStatus.FAILED);
            return syncResult;
        }


        var resultStatus = huMoResponse.getData().getStatus();
        log.info("HuMo sync result status: {}", resultStatus);

        if (huMoResponse.getData().getAuth_ref_number() != null)
            syncResult.setRRN(huMoResponse.getData().getAuth_ref_number());


        switch (HuMoStatus.valueOf(resultStatus.toUpperCase())) {
            case CANCELLED -> {

                if (!huMoResponse.getData().getAuth_action_code().equals("000")) {

                    syncResult.setStatus(ResponseStatus.FAILED);
                    syncResult.setError(
                            new CoreError(
                                    Integer.valueOf(huMoResponse.getData().getAuth_action_code()),
                                    huMoResponse.getData().getAuth_action_code()
                            )
                    );
                } else {

                    syncResult.setStatus(ResponseStatus.CANCELLED);
                    syncResult.setError(
                            new CoreError(
                                    2001,
                                    huMoResponse.getData().getAuth_action_code()
                            )
                    );
                }
                return syncResult;

            }
            case RETURNED -> {
                syncResult.setStatus(ResponseStatus.REVERSED);
                syncResult.setError(
                        new CoreError(
                                2001,
                                huMoResponse.getData().getAuth_action_code()
                        )
                );
                return syncResult;
            }
            case ACTIVE -> {
                syncResult.setIsSuccess(false);
                syncResult.setStatus(ResponseStatus.DEBIT_CONFIRM_NEED_CONFIRM);
                syncResult.setError(
                        new CoreError(
                                2006,
                                "%s transaction is in ACTIVE status".formatted(extId)
                        )
                );
                return syncResult;
            }
            case FINISHED -> {
                syncResult.setIsSuccess(true);
                syncResult.setStatus(ResponseStatus.SUCCESS);
                return syncResult;
            }
            default -> throw new InvalidDataException("%s enum for extId: %s handling not found. Contact with coder"
                    .formatted(resultStatus, extId));
        }


    }

    @Override
    public SyncResult syncABS(String refNum) {
        log.info("Starting sync ABS transaction with refNum: {}", refNum);

        if (refNum == null || refNum.isEmpty())
            throw new InvalidDataException("RefNum can not be null!");

        var sr = new SyncResult();
        sr.setRequest(Map.of("requestParam:paymentId", refNum));

        var nciResponse = nciBillingService.checkPaymentFromABS(refNum);

        sr.setResponse(nciResponse.toMap());

        if (!nciResponse.getStatus()) {
            sr.setStatus(ResponseStatus.NEED_CONFIRM);
            sr.setIsSuccess(false);
            sr.setError(
                    new CoreError(
                            2003,
                            nciResponse.getError().getMessage()
                    )
            );
            return sr;
        }

        if (nciResponse.getData().getError() != null) {
            sr.setStatus(ResponseStatus.FAILED);
            sr.setIsSuccess(false);
            sr.setError(
                    new CoreError(
                            nciResponse.getData().getError().getCode(),
                            nciResponse.getData().getError().getMessage()
                    )
            );
            return sr;
        }

        sr.setStatus(ResponseStatus.SUCCESS);
        sr.setIsSuccess(true);
        sr.setRRN(nciResponse.getData().getResult().getGeneralId());

        log.info("Return ABS sync result: {}", sr);
        return sr;
    }

    @Override
    public SyncResult syncDavrVISA(Transaction transaction) {
        log.info("Starting sync DAVR VISA payment by card: {}", transaction.getAccount());
        var sr = new SyncResult();

        LocalDate dateBefore = transaction.getCreatedDate().minusDays(1);
        LocalDate dateAfter = transaction.getCreatedDate().plusDays(1);


        var historyRequest = new VisaHistoryRequest(
                transaction.getAccount(),
                DateUtils.LocalDateConvertToFormat(dateBefore, "yyyy-MM-dd"),
                DateUtils.LocalDateConvertToFormat(dateAfter, "yyyy-MM-dd"),
                VISA_SUB_SYSTEM
        );

        sr.setRequest(historyRequest.toMap());

        var response = visaIntegrationService.history(historyRequest);
        sr.setResponse(response.toMap());

        if (!response.getStatus()) {
            sr.setStatus(ResponseStatus.FAILED);
            sr.setIsSuccess(false);
            sr.setError(response.getError());
            return sr;
        }

        if (response.getData().getError() != null) {
            sr.setStatus(ResponseStatus.FAILED);
            sr.setIsSuccess(false);
            sr.setError(response.getData().getError());
            return sr;
        }


        ArrayList<VisaHistoryResult> result = response.getData().getDetails();

        if (result.isEmpty()) {
            sr.setStatus(ResponseStatus.FAILED);
            sr.setIsSuccess(false);
            sr.setError(
                    new CoreError(
                            -10,
                            "Data not found in history response")
            );
        }

        var operationOptional = result
                .stream()
                .filter(z -> z.getInternalNumber().equals(transaction.getRefNum()))
                .findFirst();

        if (operationOptional.isEmpty()) {
            sr.setStatus(ResponseStatus.FAILED);
            sr.setIsSuccess(false);
            sr.setError(
                    new CoreError(
                            -10,
                            "Data not found in history response")
            );
            return sr;
        }


        sr.setResponse(Map.of("result", operationOptional.get()));
        sr.setStatus(ResponseStatus.SUCCESS);
        sr.setIsSuccess(true);
        sr.setRRN(transaction.getRefNum());
        return sr;
    }


    public SyncResult syncABSTransaction(Transaction transaction) {
        // not need necessary to this method, we need method which checking by parentID
        var sr = new SyncResult();

        var transitTransactionOptional = BillUtils.getBillByStatus(
                transaction,
                TranType.TRANSIT,
                ResponseStatus.SUCCESS
        );


        if (transitTransactionOptional.isEmpty()) {
            sr.setStatus(ResponseStatus.FAILED);
            sr.setIsSuccess(false);
            sr.setError(
                    new CoreError(
                            2003,
                            "Transit SUCCESS bill not found"
                    )
            );
            return sr;
        }

        var transitBill = transitTransactionOptional.get();

        String generalID = transitBill.getRefNum();
        log.info("Starting sync ABS transaction with refNum: {}", generalID);

        sr.setRequest(
                Map.of("requestParam:paymentId", generalID)
        );

        var nciResponse = nciBillingService.getTransactionById(generalID);

        sr.setResponse(nciResponse.toMap());

        if (nciResponse.getError() != null) {

            if (nciResponse.getError().getCode().equals(508)) {
                sr.setStatus(ResponseStatus.NEED_CONFIRM);
            } else {
                sr.setStatus(ResponseStatus.FAILED);
            }

            sr.setIsSuccess(false);
            sr.setError(
                    new CoreError(
                            2003,
                            nciResponse.getError().getMessage()
                    )
            );
            return sr;
        }

        if (!nciResponse.getResult().getState().equals("3")) {
            sr.setStatus(ResponseStatus.FAILED);
            sr.setIsSuccess(false);
            sr.setError(
                    new CoreError(
                            2005,
                            "Transaction: %s state: %s".formatted(generalID, nciResponse.getResult().getState())
                    )
            );
            return sr;
        }

        sr.setStatus(ResponseStatus.SUCCESS);
        sr.setIsSuccess(true);
        sr.setRRN(generalID);

        log.info("Return ABS sync result: {}", sr);
        return sr;
    }


    @Override
    public SyncResult syncMaster(String extId) {
        var syncResult = new SyncResult();
        var syncRequest = new MasterSyncByExtIdRequest(extId);

        syncResult.setRequest(syncRequest.toMap());

        var masterSyncResponse = masterGatewayClientService.syncByExtId(syncRequest);
        syncResult.setResponse(masterSyncResponse.toMap());

        if (!masterSyncResponse.getStatus()) {
            log.error("MASTER sync has error: {}", masterSyncResponse.getError());
            syncResult.setError(masterSyncResponse.getError());
            syncResult.setStatus(ResponseStatus.NEED_CONFIRM);
            return syncResult;
        }

        if (masterSyncResponse.getData().getError() != null) {
            log.error("MASTER sync response has error: {}", masterSyncResponse.getData().getError());

            if (masterSyncResponse.getData().getError().getCode().equals(1039)) {
                syncResult.setError(masterSyncResponse.getData().getError());
                syncResult.setStatus(ResponseStatus.NEED_CONFIRM);
                return syncResult;
            }

            syncResult.setError(masterSyncResponse.getData().getError());
            syncResult.setStatus(ResponseStatus.FAILED);
            return syncResult;
        }

        var result = masterSyncResponse.getData().getResult();


        switch (result.getStatus()) {
            case FAILED -> {
                var message = "Payment: %s status is FAILED".formatted(extId);
                log.error(message);
                syncResult.setRRN(result.getRefNum());
                syncResult.setError(new CoreError(2003, message));
                syncResult.setStatus(ResponseStatus.FAILED);
                return syncResult;
            }
            case REVERSE_FAILED -> {
                var message = "Payment: %s status is REVERSE_FAILED".formatted(extId);
                log.error(message);
                syncResult.setRRN(result.getRefNum());
                syncResult.setError(new CoreError(2002, message));
                syncResult.setStatus(ResponseStatus.REVERSE_FAILED);
                return syncResult;
            }
            case REVERSED -> {

                var message = "Payment reverse: %s success".formatted(extId);
                log.info(message);
                syncResult.setRRN(result.getRefNum());
                syncResult.setStatus(ResponseStatus.REVERSED);
                syncResult.setError(new CoreError(2001, message));
                return syncResult;
            }
            case SUCCESS -> {
                var message = "Payment: %s success".formatted(extId);
                log.info(message);
                syncResult.setIsSuccess(true);
                syncResult.setRRN(result.getRefNum());
                syncResult.setStatus(ResponseStatus.SENDER_SUCCESS);
                return syncResult;
            }

            default -> throw new InvalidDataException("NOT Configured status: %s for Master payment: %s handling"
                    .formatted(result.getStatus(), extId));
        }


    }
}
