package uz.repay.payment.service.business.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;
import uz.repay.common.model.dto.request.card.response.CardFullInfoResponseDTO;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.utils.CardUtils;
import uz.repay.payment.model.dto.cardService.response.TerminalResponse;
import uz.repay.payment.model.dto.core.pay.response.result.DebitResult;
import uz.repay.payment.model.dto.humo.request.HuMoDebitConfirmRequest;
import uz.repay.payment.model.dto.humo.request.HuMoDebitRequest;
import uz.repay.payment.model.dto.humo.response.HuMoBasePaymentResponse;
import uz.repay.payment.model.dto.master.request.MasterDebitRequest;
import uz.repay.payment.model.dto.uzcard.request.BalanceRequest;
import uz.repay.payment.model.dto.uzcard.request.TransPayDto;
import uz.repay.payment.model.dto.uzcard.request.UzCardBaseRequest;
import uz.repay.payment.model.dto.uzcard.request.params.TransPayPurposeParam;
import uz.repay.payment.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.payment.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.payment.model.dto.uzcard.response.result.TransPayPurposeResult;
import uz.repay.payment.model.dto.visa.request.VisaTranBaseRequest;
import uz.repay.payment.model.entity.Bill;
import uz.repay.payment.model.entity.Transaction;
import uz.repay.payment.model.enums.TranType;
import uz.repay.payment.model.property.HumoGatewayProperty;
import uz.repay.payment.service.business.DebitTransService;
import uz.repay.payment.service.core.TransactionDAO;
import uz.repay.payment.service.web.*;
import uz.repay.payment.utils.BillUtils;
import uz.repay.payment.utils.ErrorBuilder;

import java.util.*;

import static uz.repay.payment.utils.PaymentUtils.MASTER_DEFAULT_PAY_OPERATION;
import static uz.repay.payment.utils.PaymentUtils.MASTER_DEFAULT_PAY_TYPE;

@Service
@Slf4j
@RequiredArgsConstructor
public class DebitTranServiceImpl implements DebitTransService {
    private static final int UZCARD_TRANS_PAY_FALLBACK_METHOD = -2;
    private final UzCardIntegrationService uzCardIntegrationService;
    private final CardService cardService;
    private final HumoGatewayProperty humoGatewayProperty;
    private final HuMoGatewayService huMoGatewayService;
    private final VisaIntegrationService visaIntegrationService;
    private final TransactionDAO transactionDAO;
    private final MasterGatewayClientService masterGatewayClientService;
    private final ObjectMapper objectMapper;


    @Override
    public DebitResult pay(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse, Integer method) {

        switch (requestDTO.getDebitParams().getProcessing()) {
            case UZCARD, SVGATE_AUTOPAY -> {
                return debitFromUzCard(requestDTO, extId, ePosResponse, method);
            }
            case HUMO -> {
                return debitFromHuMo(requestDTO, extId, ePosResponse);
            }
            case DAVR_VISA -> {
                return debitFromDAVRVisa(requestDTO, extId, ePosResponse);
            }
            case DAVR_MASTER_CARD -> {
                return debitFromMaster(requestDTO, extId, ePosResponse);
            }

            default -> {
                return new DebitResult(
                        new DebitResult.ErrorResult("Pay method not detected for processing: %s".formatted(requestDTO.getDebitParams().getProcessing()), ResponseStatus.FAILED)
                );
            }
        }


    }

    public DebitResult debitFromUzCard(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse, Integer method) {
        log.info("Handle uzCard request/response for: {}", extId);
        var dr = new DebitResult();

        if (!requestDTO.getDebitParams().getType().equals(CardType.token)) {
            var errorMessage = "Internal back error: Debit for: %s only from TOKEN!".formatted(requestDTO.getDebitParams().getProcessing());
            log.error(errorMessage);
            dr.setError(new DebitResult.ErrorResult(
                    errorMessage,
                    ResponseStatus.FAILED
            ));
            return dr;
        }

       /* SimpleResponse<List<CardBaseResult>> cardInfoResponse = null;
        try {
            log.info(" GET_CARD_INFO_REQUEST card: {} ", requestDTO.getDebitParams().getCard());
            cardInfoResponse = cardService.getCardInfo(new BalanceRequest(new ArrayList<>(List.of(requestDTO.getDebitParams().getCard()))));
            log.info("GET_CARD_INFO_RESPONSE: {}", objectMapper.writeValueAsString(cardInfoResponse));
        } catch (Exception e) {
            log.info("GET_CARD_INFO_ERROR: {}", e.getMessage());
        }

        String type;
        if (cardInfoResponse != null && cardInfoResponse.getData() != null && !cardInfoResponse.getData().isEmpty()) {
            CardBaseResult cardInfoResult = cardInfoResponse.getData().getFirst();

            if (cardInfoResult != null) {
                type = cardInfoResult.getCardType(); //Private, Corporate, Pride
                log.info(" CARD_TYPE_GET_BY_CARD_INFO: type: {}, card: {}", type, requestDTO.getDebitParams().getCard());
                if ("Corporate".equals(type)) {
                    var errorMessage = "Only Private and Pride cards can be debited for loan";
                    log.error(errorMessage);
                    dr.setError(new DebitResult.ErrorResult(
                            errorMessage,
                            ResponseStatus.FAILED
                    ));
                    return dr;
                }
            }
        }*/


        boolean isConfirmed = false;
        if (!Objects.equals(method, UZCARD_TRANS_PAY_FALLBACK_METHOD) && Processing.SVGATE_AUTOPAY == requestDTO.getDebitParams().getProcessing()) {
            var uzcardResponse = getCardFromCardService(requestDTO);
            if (uzcardResponse != null && uzcardResponse.getData() != null && uzcardResponse.getData().getResult() != null) {
                isConfirmed = uzcardResponse.getData().getResult().getIsConfirmed();
            }
        }

        SimpleResponse<UzCardBaseResponse<TransPayPurposeResult>> uzCardResponse = null;

        if (Objects.equals(method, UZCARD_TRANS_PAY_FALLBACK_METHOD)) {
            var port = 1;
            String stan = String.format("%06d", new Random().nextInt(999999));
            var request = transPayRequestBuilder(requestDTO, extId, ePosResponse, stan, port);
            dr.setRequest(request.toMap());
            try {
                uzCardResponse = uzCardIntegrationService.pay2(request.getParams());
                dr.setResponse(uzCardResponse.toMap());
            } catch (Exception e) {
                log.error(" ------ UZCARD_TRANS_PAY_ERROR ------ }");
                dr.setResponse(Map.of("Error from uzcard transPay", e));
                dr.setError(new DebitResult.ErrorResult(
                        "Uzcard trans.pay error: %s".formatted(e.getMessage()),
                        ResponseStatus.FAILED
                ));
                return dr;
            }
        } else {
            var purposeRequest = requestBuilder(requestDTO, extId, ePosResponse, isConfirmed);
            dr.setRequest(purposeRequest.toMap());
            try {
                uzCardResponse = uzCardIntegrationService.pay(purposeRequest);
                dr.setResponse(uzCardResponse.toMap());
            } catch (Exception e) {
                log.error("Trans.pay.purpose.error: {}", e.getMessage());
            }
        }

        if (uzCardResponse == null) {
            dr.setError(
                    new DebitResult.ErrorResult(
                            "Uzcard pay error",
                            ResponseStatus.FAILED
                    )
            );
            return dr;
        }

        if (!Boolean.TRUE.equals(uzCardResponse.getStatus())) {
            var responseError = uzCardResponse.getError();
            var responseErrorMessage = responseError != null ? responseError.getMessage() : "Uzcard pay error";
            var responseErrorCode = responseError != null ? responseError.getCode() : -1;
            log.error("UzCard has error: {}", responseErrorMessage);
            if (Objects.equals(responseErrorCode, 580)) {
                dr.setError(new DebitResult.ErrorResult(
                        responseErrorMessage,
                        ResponseStatus.NEED_CONFIRM
                ));
            } else {
                dr.setError(new DebitResult.ErrorResult(
                        responseErrorMessage,
                        ResponseStatus.FAILED
                ));
            }
            dr.setResultCode(responseErrorCode);
            return dr;
        }

        if (uzCardResponse.getData() == null) {
            dr.setError(
                    new DebitResult.ErrorResult(
                            "Uzcard pay error: empty response data",
                            ResponseStatus.FAILED
                    )
            );
            return dr;
        }

        if (uzCardResponse.getData().getError() != null) {
            log.error("UzCard has error: {}", uzCardResponse.getData().getError().getMessage());
            var errorMessageResult = cardService.findAllStatus();
            String errorMessage = ErrorBuilder.getErrorCodeDescription(errorMessageResult, uzCardResponse.getData().getError().getCode().toString(), Processing.UZCARD);

            if (uzCardResponse.getData().getError().getCode().equals(505)) {
                dr.setError(
                        new DebitResult.ErrorResult(
                                errorMessage,
                                ResponseStatus.NEED_CONFIRM
                        )
                );
            } else {
                dr.setError(
                        new DebitResult.ErrorResult(
                                errorMessage,
                                ResponseStatus.FAILED
                        )
                );
            }

            dr.setResultCode(uzCardResponse.getData().getError().getCode());
            return dr;
        }

        var result = uzCardResponse.getData().getResult();

        if (result == null) {
            dr.setError(
                    new DebitResult.ErrorResult(
                            "Uzcard pay error: empty result",
                            ResponseStatus.FAILED
                    )
            );
            return dr;
        }

        if (!Objects.equals(result.getResp(), 0) && !Objects.equals(result.getStatus(), "OK")) {
            log.error("UzCard has error: {}", uzCardResponse.getData().getResult().getRespText());
            var errorMessageResult = cardService.findAllStatus();
            String errorMessage = ErrorBuilder.getErrorCodeDescription(errorMessageResult, result.getResp().toString(), Processing.UZCARD);

            if (ErrorBuilder
                    .uzCardNeedConfirmCodes()
                    .stream()
                    .anyMatch(z -> Objects.equals(z, result.getResp())))
                dr.setError(new DebitResult.ErrorResult(
                        errorMessage,
                        ResponseStatus.UZCARD_NEED_CONFIRM
                ));
            else
                dr.setError(new DebitResult.ErrorResult(
                        errorMessage,
                        ResponseStatus.FAILED
                ));

            dr.setResultCode(result.getResp());
            return dr;
        }

        dr.setResultCode(0);
        dr.setIsSuccess(true);
        dr.setRrn(result.getRefNum());
        dr.setPaymentID(result.getRefNum());

        return dr;
    }


    private static UzCardBaseRequest<TransPayPurposeParam> requestBuilder(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse, boolean carIsConfirmed) {
        var request = getTransPayPurposeParamUzCardBaseRequest(requestDTO, extId, ePosResponse, carIsConfirmed);

        log.info(" ------- UZCARD_DEBIT_REQUEST_TRANS_PAY.PURPOSE ------- :\n\n" + """
                        {
                         	"id": {},
                         	"jsonrpc": "{}",
                         	"method": "{}",
                         	"params": {
                         		"tran": {
                         			"purpose": "{}",
                         			"receiverId": "{}",
                         			"cardId": "{}",
                         			"amount": {},
                         			"commission": {},
                         			"currency": "{}",
                         			"ext": "{}",
                         			"merchantId": "{}",
                         			"terminalId": "{}"
                         		}
                         	}
                         }""",
                request.getId(),
                request.getJsonRPC(),
                request.getMethod(),
                request.getParams().getTran().getPurpose(),
                request.getParams().getTran().getReceiverId(),
                request.getParams().getTran().getCardId(),
                request.getParams().getTran().getAmount(),
                request.getParams().getTran().getCommission(),
                request.getParams().getTran().getCurrency(),
                request.getParams().getTran().getExt(),
                request.getParams().getTran().getMerchantId(),
                request.getParams().getTran().getTerminalId()
        );

        return request;
    }


    private static UzCardBaseRequest<TransPayDto> transPayRequestBuilder(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse, String stan, Integer port) {
        var request = getTransPayParamUzCardBaseRequest(requestDTO, extId, ePosResponse, stan, port);

        log.info(" ------- UZCARD_DEBIT_REQUEST_TRANS_PAY --------:\n\n" + """
                        {
                         	"id": {},
                         	"jsonrpc": "{}",
                         	"method": "{}",
                         	"params": {
                         		"tran": {
                         			"cardId": "{}",
                         			"amount": {},
                         			"ext": "{}",
                         			"merchantId": "{}",
                         			"terminalId": "{}"
                         		}
                         	}
                         }""",
                request.getId(),
                request.getJsonRPC(),
                request.getMethod(),
                request.getParams().getCardId(),
                request.getParams().getAmount(),
                request.getParams().getExt(),
                request.getParams().getMerchant(),
                request.getParams().getTerminalId()
        );

        return request;
    }

    private static UzCardBaseRequest<TransPayPurposeParam> getTransPayPurposeParamUzCardBaseRequest(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse, boolean carIsConfirmed) {
        Processing processing = requestDTO.getDebitParams().getProcessing();
        var purpose = (Processing.SVGATE_AUTOPAY == processing && carIsConfirmed) || Processing.UZCARD == processing ? "loan" : "recurrent";

        var tran = new TransPayPurposeParam.Tran(
                purpose,
                requestDTO.getLoanExtId(),
                requestDTO.getDebitParams().getCard(),
                requestDTO.getAmount(),
                0,
                "860",
                extId,
                ePosResponse.getMerchantId(),
                ePosResponse.getTerminalId()
        );


        var param = new TransPayPurposeParam(tran);

        return new UzCardBaseRequest<>(
                extId,
                "2.0",
                "trans.pay.purpose",
                param);
    }

    private static UzCardBaseRequest<TransPayDto> getTransPayParamUzCardBaseRequest(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse, String stan, Integer port) {
        TransPayDto dto = new TransPayDto()
                .setExt(extId)
                .setPort(port)
                .setTerminalId(ePosResponse.getTerminalId())
                .setMerchant(ePosResponse.getMerchantId())
                .setStan(stan)
                .setCardId(requestDTO.getDebitParams().getCard())
                .setAmount(requestDTO.getAmount());

        return new UzCardBaseRequest<>(
                extId,
                "2.0",
                "trans.pay",
                dto);
    }


    public DebitResult debitFromHuMo(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse) {
        log.info("Handle huMo request/response for: {}", extId);
        var dr = new DebitResult();

        if (!requestDTO.getDebitParams().getType().equals(CardType.pan)) {
            var errorMessage = "Internal back error: Debit for: %s only from PAN!".formatted(requestDTO.getDebitParams().getProcessing());
            log.error(errorMessage);
            dr.setError(new DebitResult.ErrorResult(
                    errorMessage,
                    ResponseStatus.FAILED
            ));
            return dr;
        }


        var cardResponse = getCardFromCardService(requestDTO);


        if (!cardResponse.getStatus()) {
            log.error("Card Service has error: {}", cardResponse.getError().getMessage());
            dr.setError(new DebitResult.ErrorResult(
                    cardResponse.getError().getMessage(),
                    ResponseStatus.FAILED
            ));
            return dr;
        }

        if (cardResponse.getData().getError() != null) {
            log.error("Card Service has error: {}", cardResponse.getData().getError().getMessage());
            dr.setError(new DebitResult.ErrorResult(
                    cardResponse.getData().getError().getMessage(),
                    ResponseStatus.FAILED
            ));
            return dr;
        }

        var card = cardResponse.getData().getResult();

        var huMoDebitRequest = new HuMoDebitRequest(
                extId,
                CardUtils.decryptPan(requestDTO.getDebitParams().getCard()),
                card.getExpiry(),
                requestDTO.getAmount(),
                ePosResponse.getMerchantId(),
                ePosResponse.getTerminalId(),
                humoGatewayProperty.getOriginator(),
                BillUtils.HUMO_GATE_SUBSYSTEM
        );
        log.info("Prepared huMo request: {}", huMoDebitRequest);

        dr.setRequest(huMoDebitRequest.toMap());

        var huMoResponse = huMoGatewayService.debit(
                huMoDebitRequest
        );

        dr.setResponse(huMoResponse.toMap());

        if (!huMoResponse.getStatus()) {
            log.error("Humo Debit method has error: {}", huMoResponse.getError().getMessage());

            if (huMoResponse.getError().getCode().equals(580)) {
                dr.setError(new DebitResult.ErrorResult(
                        huMoResponse.getError().getMessage(),
                        ResponseStatus.NEED_CONFIRM
                ));
            } else {
                dr.setError(new DebitResult.ErrorResult(
                        huMoResponse.getError().getMessage(),
                        ResponseStatus.FAILED
                ));
            }

            return dr;
        }

        if (huMoResponse.getData().getError() != null) {
            log.error("Humo Debit method has error: {}", huMoResponse.getData().getError().getMessage());
            var errorMessageResult = cardService.findAllStatus();
            String errorMessage = ErrorBuilder.getErrorCodeDescription(errorMessageResult, huMoResponse.getData().getError().getCode().toString(), Processing.HUMO);

            if (huMoResponse.getData().getError().getCode().equals(505)) {
                dr.setError(new DebitResult.ErrorResult(
                        errorMessage,
                        ResponseStatus.NEED_CONFIRM
                ));
            } else {
                dr.setError(new DebitResult.ErrorResult(
                        errorMessage,
                        ResponseStatus.FAILED
                ));
            }

            dr.setResultCode(huMoResponse.getData().getError().getCode());
            return dr;
        }


        var result = huMoResponse.getData();

        dr.setResultCode(0);
        dr.setIsSuccess(true);
        dr.setRrn(result.getAuth_ref_number());
        dr.setPaymentID(result.getPaymentID());

        log.info("Debit Active finished with authRefNum: {}", result.getAuth_ref_number());
        return dr;
    }

    private SimpleResponse<ResultData<CardFullInfoResponseDTO>> getCardFromCardService(PayRequestDTO requestDTO) {
        var cardRequest = new CardFullInfoRequestDTO(
                requestDTO.getDebitParams().getCard()
        );
        log.info("Prepared get card service: {}", cardRequest);

        var cardResponse = cardService.getCardByPan(cardRequest);
        log.info("\n --------------Card Response:{} -------------- \n", cardResponse);
        return cardResponse;
    }


    public DebitResult debitFromDAVRVisa(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse) {
        log.info("Handle visa request/response for: {}", extId);
        var dr = new DebitResult();

        if (!requestDTO.getDebitParams().getType().equals(CardType.pan)) {
            var errorMessage = "Internal back error: Debit for: %s only from PAN!".formatted(requestDTO.getDebitParams().getProcessing());
            log.error(errorMessage);
            dr.setError(new DebitResult.ErrorResult(
                    errorMessage,
                    ResponseStatus.FAILED
            ));
            return dr;
        }


        var visaDebitRequest = new VisaTranBaseRequest(
                CardUtils.decryptPan(requestDTO.getDebitParams().getCard()),
                requestDTO.getAmount()
        );

        log.info("Prepared visa request: {}", visaDebitRequest);

        dr.setRequest(visaDebitRequest.toMap());

        var visaResponse = visaIntegrationService.debit(
                visaDebitRequest
        );

        dr.setResponse(visaResponse.toMap());

        if (!visaResponse.getStatus()) {
            log.error("Visa Debit method has error: {}", visaResponse.getError().getMessage());
            if (visaResponse.getError().getCode().equals(580)) {
                dr.setError(new DebitResult.ErrorResult(
                        visaResponse.getError().getMessage(),
                        ResponseStatus.NEED_CONFIRM
                ));
            } else {
                dr.setError(new DebitResult.ErrorResult(
                        visaResponse.getError().getMessage(),
                        ResponseStatus.FAILED
                ));
            }

            return dr;
        }

        if (visaResponse.getData().getError() != null) {
            log.error("visaResponse Debit method has error: {}", visaResponse.getData().getError().getMessage());
            var errorMessageResult = cardService.findAllStatus();
            String errorMessage = ErrorBuilder.getErrorCodeDescription(errorMessageResult, visaResponse.getData().getError().getCode().toString(), Processing.DAVR_VISA);

            if (visaResponse.getData().getError().getCode().equals(505)) {
                dr.setError(new DebitResult.ErrorResult(
                        errorMessage,
                        ResponseStatus.NEED_CONFIRM
                ));
            } else {
                dr.setError(new DebitResult.ErrorResult(
                        errorMessage,
                        ResponseStatus.FAILED
                ));
            }

            dr.setResultCode(visaResponse.getData().getError().getCode());
            return dr;
        }


        var result = visaResponse.getData().getResult();

        dr.setResultCode(Integer.valueOf(result.getResponseCode()));
        dr.setIsSuccess(true);
        dr.setRrn(result.getInternalNo());
        dr.setPaymentID(result.getInternalNo());

        log.info("Visa Debit finished with authRefNum: {}", result.getInternalNo());
        return dr;
    }


    public SimpleResponse<HuMoBasePaymentResponse> huMoDebitConfirm(Bill debitBill, Transaction transaction) {
        log.info("Try to confirm HUMO active payment: {}", debitBill.getRefNum());

        var request = new HuMoDebitConfirmRequest(
                debitBill.getRefNum(),
                humoGatewayProperty.getOriginator(),
                BillUtils.HUMO_GATE_SUBSYSTEM
        );

        var debitConfirmBill = new Bill(
                debitBill.getAmount(),
                debitBill.getDebitFrom(),
                debitBill.getCreditTo(),
                debitBill.getRefNum(),
                debitBill.getMerchantId(),
                debitBill.getTerminalId(),
                TranType.DEBIT_CONFIRM,
                ResponseStatus.PENDING,
                request.toMap(),
                transaction,
                Currency.UZS
        );
        transaction.getBills().add(debitConfirmBill);


        var response = huMoGatewayService.debitConfirm(request);

        debitConfirmBill.setResponse(response.toMap());
        log.info("For: {} Debit Confirm response: {}", request.getPaymentId(), response);


        if (!response.getStatus()) {
            if (response.getError().getCode().equals(580)) {
                handleFailedResponse(response.getError().getMessage(), transaction, debitConfirmBill,
                        ResponseStatus.DEBIT_CONFIRM_NEED_CONFIRM);
                return new SimpleResponse<>(580, response.getError().getMessage());
            } else {
                handleFailedResponse(response.getError().getMessage(), transaction, debitConfirmBill,
                        ResponseStatus.FAILED);
                return new SimpleResponse<>(777, response.getError().getMessage());
            }

        }

        if (response.getData().getError() != null) {
            var errorCode = response.getData().getError().getCode();

            if (errorCode.equals(505)) // in humo_gateway throws 505 code if service can't connect to huMo
                handleFailedResponse(response.getData().getError().getMessage(), transaction, debitConfirmBill,
                        ResponseStatus.DEBIT_CONFIRM_NEED_CONFIRM);
            else
                handleFailedResponse(response.getData().getError().getMessage(), transaction, debitConfirmBill,
                        ResponseStatus.S_NEED_CONFIRM);
            return new SimpleResponse<>(778, response.getData().getError().getMessage());
        }

        if (response.getData().getAction() == null || response.getData().getAction() != 10) {
            String message = "Action field is null or not equal to 10";
            handleFailedResponse(message, transaction, debitConfirmBill,
                    ResponseStatus.S_NEED_CONFIRM);
            return new SimpleResponse<>(779, message);

        }

        debitConfirmBill.setStatus(ResponseStatus.SUCCESS);
        transaction.setStatus(ResponseStatus.SUCCESS);
        transactionDAO.save(transaction);
        return new SimpleResponse<>(response.getData());
    }

    private void handleFailedResponse(String message, Transaction transaction, Bill debitConfirmBill, ResponseStatus status) {
        log.error("HuMo Debit Confirm method has error: {}", message);
        transaction.setStatus(status);
        transaction.setNote(message);
        debitConfirmBill.setStatus(ResponseStatus.FAILED);
        transactionDAO.save(transaction);
    }


    public DebitResult debitFromMaster(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse) {
        log.info("Handle debit operation from master card for: {}", extId);
        var dr = new DebitResult();

        if (!requestDTO.getDebitParams().getType().equals(CardType.token)) {
            var errorMessage = "Internal back error: Debit for: %s only from TOKEN!".formatted(requestDTO.getDebitParams().getProcessing());
            log.error(errorMessage);
            dr.setError(new DebitResult.ErrorResult(
                    errorMessage,
                    ResponseStatus.FAILED
            ));
            return dr;
        }


        var request = masterRequestBuilder(requestDTO, extId, ePosResponse);
        dr.setRequest(request.toMap());

        var masterResponse = masterGatewayClientService.debit(request);
        dr.setResponse(masterResponse.toMap());

        if (!masterResponse.getStatus()) {
            log.error("Master result has error: {}", masterResponse.getError().getMessage());

            if (masterResponse.getError().getCode().equals(580)) {
                dr.setError(new DebitResult.ErrorResult(
                        masterResponse.getError().getMessage(),
                        ResponseStatus.NEED_CONFIRM
                ));
            } else {
                dr.setError(new DebitResult.ErrorResult(
                        masterResponse.getError().getMessage(),
                        ResponseStatus.FAILED
                ));
            }

            return dr;
        }

        if (masterResponse.getData().getError() != null) {
            log.error("Master response has error: {}", masterResponse.getData().getError().getMessage());
            var errorMessageResult = cardService.findAllStatus();
            String errorMessage = ErrorBuilder.getErrorCodeDescription(errorMessageResult, masterResponse.getData().getError().getCode().toString(), Processing.DAVR_MASTER_CARD);

            if (masterResponse.getData().getError().getCode().equals(1039)) { // time out in bpc connect
                dr.setError(
                        new DebitResult.ErrorResult(
                                errorMessage,
                                ResponseStatus.NEED_CONFIRM
                        )
                );
            } else {
                dr.setError(
                        new DebitResult.ErrorResult(
                                errorMessage,
                                ResponseStatus.FAILED
                        )
                );
            }

            dr.setResultCode(masterResponse.getData().getError().getCode());
            return dr;
        }

        var result = masterResponse.getData().getResult();

        dr.setResultCode(0);
        dr.setIsSuccess(true);
        dr.setRrn(result.getRefNum());
        dr.setPaymentID(result.getRefNum());

        return dr;
    }


    private MasterDebitRequest masterRequestBuilder(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse) {
        var request = new MasterDebitRequest(
                requestDTO.getAmount(),
                ePosResponse.getMerchantId(),
                ePosResponse.getTerminalId(),
                requestDTO.getDebitParams().getCard(),
                extId,
                MASTER_DEFAULT_PAY_TYPE,
                0L,
                MASTER_DEFAULT_PAY_OPERATION
        );

        var message = """
                {
                    "id": %s,
                    "params": {
                        "amount": %s,
                        "merchantId": "%s",
                        "terminalId": "%s",
                        "token": "%s",
                        "extId": "%s",
                        "type": "%s",
                        "commission": %s,
                        "operation" : "%s"
                    }
                }"""
                .formatted(extId,
                        request.getAmount(),
                        request.getMerchantId(),
                        request.getTerminalId(),
                        request.getToken(),
                        request.getExtId(),
                        request.getType(),
                        request.getCommission(),
                        request.getOperation()

                );

        log.info("Prepared master debit request: {}", message);

        return request;
    }
}
