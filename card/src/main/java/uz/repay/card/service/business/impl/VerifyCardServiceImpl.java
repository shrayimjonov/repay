package uz.repay.card.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.OTPRequestDTO;
import uz.repay.card.model.dto.core.request.card.VerifyRequestDTO;
import uz.repay.card.model.dto.core.response.card.OTPResponseDTO;
import uz.repay.card.model.dto.core.response.card.VerifyResponseDTO;
import uz.repay.card.model.dto.core.response.card.result.CardConfirmResult;
import uz.repay.card.model.dto.humo.request.HuMoCardConfirmRequest;
import uz.repay.card.model.dto.humo.request.HuMoCardVerifyRequest;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.humo.response.HumoTokenResponse;
import uz.repay.card.model.dto.master.request.MasterOTPConfirmRequest;
import uz.repay.card.model.dto.master.request.MasterOTPVerifyRequest;
import uz.repay.card.model.dto.master.request.MasterP2PInfoRequest;
import uz.repay.card.model.dto.sms.request.SMSRequest;
import uz.repay.card.model.dto.uzcard.request.CheckCard2PinflRequest;
import uz.repay.card.model.dto.uzcard.request.OTPRequest;
import uz.repay.card.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.card.model.dto.uzcard.request.VerifyRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.dto.uzcard.response.result.P2PInfoResult;
import uz.repay.card.model.dto.visa.request.VisaCardInfoRequest;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.entity.Loan;
import uz.repay.card.redis.entity.Message;
import uz.repay.card.service.business.VerifyCardService;
import uz.repay.card.service.business.component.NotificationComponent;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.card.service.core.LoanDAO;
import uz.repay.card.service.core.MessageRedisService;
import uz.repay.card.service.web.*;
import uz.repay.card.util.SMSUtils;
import uz.repay.common.exception.*;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardCommon;
import uz.repay.common.utils.CardUtils;
import uz.repay.common.utils.CommonUtils;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static uz.repay.card.util.ServiceUtils.improvePhone;


@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyCardServiceImpl implements VerifyCardService {
    private final UzCardService uzCardService;
    private final SMSService smsService;
    private final NotificationComponent notificationComponent;
    private final HuMoClientService huMoClientService;
    private final MessageRedisService messageRedisService;
    private final CardCommon cardCommon;
    private final CardDAO cardDAO;
    private final LoanDAO loanDAO;
    private final LoanCardDAO loanCardDAO;
    private final VisaMiddlewareService visaMiddlewareService;
    private final MasterClientService masterClientService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public ResultData<OTPResponseDTO> sendOTP(OTPRequestDTO requestDTO) {
        log.info("Try to add new card: {}", requestDTO.getCard());
        var loan = validateLoanAndCard(requestDTO);

        var processing = getCardProcessing(requestDTO);

        switch (processing) {
            case UZCARD -> {
                return getUZCARDOTPResponse(requestDTO, loan);
            }
            case HUMO -> {
                //   return getHUMOOTPResponse(requestDTO);
                return getHuMoOTPResponseV2(requestDTO, loan);
            }
            case DAVR_VISA -> {
                return getVisaDAVRResponse(requestDTO, loan);
            }
            case DAVR_MASTER_CARD -> {
                return getMasterOTPResponse(requestDTO, loan);
            }
            default -> {
                return new ResultData<>(ResultState.IncorrectProcessing, ResultState.IncorrectProcessing.getMessage());
            }
        }
    }


    private Loan validateLoanAndCard(OTPRequestDTO requestDTO) {
        var loanOptional = loanDAO.findOptionalById(requestDTO.getLoanId());

        if (loanOptional.isEmpty())
            throw new EntityNotFoundException("Loan with id: %d not found in database".formatted(requestDTO.getLoanId()));

        SecurityUtils.checkUserRequestedBranch(loanOptional.get().getExtId());

        var loan = loanOptional.get();
        log.info("Start filter cards list for loan: {}", loan.getExtId());

        var cardOptional = loan.getLoanCards()
                .stream()
                .filter(
                        z -> z.getCard().getPan().equals(CardUtils.encryptPan(requestDTO.getCard()))
                                & z.getCard().getIsActive()
                )
                .findFirst();

        if (cardOptional.isPresent())
            throw new LoanCardException("Card: %s to Loan: %s already added!".formatted(requestDTO.getCard(), loan.getExtId()));

        return loanOptional.get();
    }

    private Processing getCardProcessing(OTPRequestDTO requestDTO) {
        var processing = cardCommon.detectCardProcessing(requestDTO.getCard());
        log.info("{} processing -> {}", requestDTO.getCard(), processing);
        return processing;
    }

    private ResultData<OTPResponseDTO> getHUMOOTPResponse(@NonNull OTPRequestDTO requestDTO) {
        var humoInfo = huMoClientService.getCardInfo(new HumoCardInfoRequest(requestDTO.getCard()));

        return handleHumoCardInfo(humoInfo, requestDTO);
    }


    private ResultData<OTPResponseDTO> getUZCARDOTPResponse(@NonNull OTPRequestDTO requestDTO, Loan loan) {
        log.info("Starting uzCard send otp method with request: {}", requestDTO);
        var response = uzCardService.OTPSend(new OTPRequest(requestDTO));

        if (response.getError() != null)
            return new ResultData<>(ResultState.CardNotFound, response.getError().getMessage());

        saveCard(requestDTO, Processing.UZCARD);

        saveUzCardOTPInRedis(requestDTO, response.getResult().getId(),
                response.getResult().getPhoneMask(),
                Processing.UZCARD, loan.getExtId());

        ResultData<OTPResponseDTO> responseDTO = new ResultData<>(
                new OTPResponseDTO(
                        response.getResult().getId(),
                        response.getResult().getPhoneMask(),
                        Processing.UZCARD
                )
        );

        log.info("Finished uzCard send otp method with response dto: {}", responseDTO);
        return responseDTO;
    }

    private void saveCard(OTPRequestDTO requestDTO, Processing processing) {

        if (processing.equals(Processing.UZCARD)) {
            checkUzCardByTokenAndSave(requestDTO, processing);
            return;
        }

        if (processing.equals(Processing.DAVR_MASTER_CARD)) {
            checkMasterCardByTokenAndSave(requestDTO, processing);
            return;
        }

        if (processing.equals(Processing.HUMO)) {
            var huMoTokenResponse = getHumoTokenResponse(requestDTO.getCard());

            if (huMoTokenResponse == null)
                throw new CardException("Can't catch huMo token. Card: %s".formatted(CardUtils.maskPanDigits(requestDTO.getCard())));

            checkAndSave(requestDTO, processing, huMoTokenResponse.maskedPan());
        }


    }


    private void checkUzCardByTokenAndSave(OTPRequestDTO requestDTO, Processing processing) {
        var p2pInfoResult = getP2PInfo(requestDTO.getCard());

        if (!p2pInfoResult.getStatus())
            throw new CardException(p2pInfoResult.getError().getMessage());

        var p2pInfo = p2pInfoResult.getData();

        if (p2pInfo.getError() != null)
            throw new CardException(p2pInfo.getError().getMessage());


        var token = p2pInfo.getResult().getId();

        checkAndSave(requestDTO, processing, token);
    }

    private void checkMasterCardByTokenAndSave(OTPRequestDTO requestDTO, Processing processing) {
        var request = new MasterP2PInfoRequest(
                requestDTO.getCard()
        );

        var p2pInfoResult = masterClientService.p2pInfo(request);


        if (!p2pInfoResult.getStatus())
            throw new CardException(p2pInfoResult.getError().getMessage());

        var p2pInfo = p2pInfoResult.getData();

        if (p2pInfo.getError() != null)
            throw new CardException(p2pInfo.getError().getMessage());


        var token = p2pInfo.getResult().getToken();

        checkAndSave(requestDTO, processing, token);

    }

    private void checkAndSave(OTPRequestDTO requestDTO, Processing processing, String token) {
        var tokenOptional = cardDAO.findCardByToken(token);

        if (tokenOptional.isEmpty()) {
            var preparedCard = new Card(requestDTO.getCard(), requestDTO.getExpiry(), processing, token);
            cardDAO.create(preparedCard);
        } else {
            var card = tokenOptional.get();
            card.setPan(CardUtils.encryptPan(requestDTO.getCard()));
            cardDAO.update(card);
        }
    }

    private SimpleResponse<UzCardBaseResponse<P2PInfoResult>> getP2PInfo(String card) {
        try {
            var p2pInfoRequest = new P2PInfoRequest(card);

            return new SimpleResponse<>(uzCardService.getP2PInfo(p2pInfoRequest));
        } catch (Exception e) {
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), e.getMessage());
        }
    }

    private void saveUzCardOTPInRedis(OTPRequestDTO requestDTO, String id,
                                      String phone, Processing processing,
                                      String loanExtId) {
        messageRedisService.createOTPV3(
                id,
                SMSUtils.getRandomCode(),
                phone,
                requestDTO.getCard(),
                processing,
                requestDTO.getLoanId(),
                loanExtId
        );
    }

    @Deprecated
    private ResultData<OTPResponseDTO> handleHumoCardInfo(HumoCardInfoResponse humoInfo, OTPRequestDTO requestDTO) {
        if (humoInfo.error() != null
                || humoInfo.result() == null
                || humoInfo.result().mb() == null
                || humoInfo.result().mb().phone() == null) {
            log.error("Phone wrong format!");
            return new ResultData<>(ResultState.PhoneNotFound, ResultState.PhoneNotFound.getMessage());
        }

        var phone = humoInfo.result().mb().phone();

        cardDAO.create(humoInfo, false);

        if (!requestDTO.getExpiry().equals(humoInfo.result().card().expiry()))
            throw new InvalidDataException("Card expiry date is wrong!");

        var code = SMSUtils.getRandomCode();

        var message = messageRedisService.createOTP(String.valueOf(System.currentTimeMillis()), code,
                phone,
                requestDTO.getCard(),
                Processing.HUMO,
                requestDTO.getLoanId()
        );


        smsService.sendV2(
                new SMSRequest(
                        humoInfo.result().mb().phone(),
                        SMSUtils.generateOTPMessage(message.getCode())
                ));


        return new ResultData<>(
                new OTPResponseDTO(
                        "******" + phone.substring(phone.length() - 4),
                        message.getId(),
                        Processing.HUMO,
                        false));
    }


    @Override
    public ResultData<VerifyResponseDTO> verify(VerifyRequestDTO requestDTO) {
        log.info("Verify new card with id : {} in type: {}", requestDTO.getId(), requestDTO.getType());
        if (requestDTO.getType() == Processing.UZCARD)
            return handleVerifyUzCard(requestDTO);
        else if (requestDTO.getType() == Processing.HUMO)
            // return handleVisaVerify(requestDTO);
            return handleVerifyHuMoV2(requestDTO);
        else if (requestDTO.getType() == Processing.DAVR_VISA)
            return handleVisaVerify(requestDTO);
        else if (requestDTO.getType().equals(Processing.DAVR_MASTER_CARD))
            return handleVerifyMaster(requestDTO);
        else
            return new ResultData<>(ResultState.IncorrectProcessing, ResultState.IncorrectProcessing.getMessage());
    }


    private ResultData<VerifyResponseDTO> handleVisaVerify(VerifyRequestDTO requestDTO) {
        log.info("Starting verify visa method with request: {}", requestDTO);

        var result = addVisaToDB(requestDTO);

        saveCardToLoanCard(result);

        VerifyResponseDTO verifyResponseDTO = new VerifyResponseDTO(result.getCard());

        log.info("Finished verify visa method with response dto: {}", verifyResponseDTO);
        return new ResultData<>(verifyResponseDTO);
    }

    @Override
    public CardConfirmResult addVisaToDB(VerifyRequestDTO requestDTO) {
        var cardRedis = findCardByRedisId(requestDTO);

        var cardOptional = cardDAO.findCardByPan(cardRedis.getCard());

        if (cardOptional.isEmpty())
            throw new EntityNotFoundException(ResultState.CardNotFound.getMessage());

        var card = cardOptional.get();

        updateCard4Humo(card);

        return new CardConfirmResult(
                card,
                requestDTO,
                cardRedis.getLoanId()
        );
    }

    private void saveCardToLoanCard(CardConfirmResult result) {
        var loan = loanDAO.findById(result.getLoanId());


        Payer payer = result.getVerifyRequestDTO().getPayer();

        if (result.getCard().getProcessing().equals(Processing.UZCARD))
            payer = detectPayerTypeByCheckingCardToPINFL(result, loan, payer);


        loanCardDAO.createV2(loan, result.getCard(), payer, AddType.MANUAL, true);

//        applicationEventPublisher.publishEvent(
//                new CardAttachedEventDTO(
//                        loan.getExtId(),
//                        result.getCard().getPhone(),
//                        loan.getLanguage(),
//                        result.getCard().getMaskedPan(),
//                        result.getCard().getHolder()
//                        )
//        );
    }

    private Payer detectPayerTypeByCheckingCardToPINFL(CardConfirmResult result, Loan loan, Payer payer) {
        var checkPINFLRequest = new CheckCard2PinflRequest(
                result.getCard().getToken(),
                loan.getPinfl()
        );
        var checkPINFLResult = uzCardService.checkCardIsBelongToPersonCode(checkPINFLRequest);

        if (checkPINFLResult.getResult() != null) {
            if (checkPINFLResult.getResult().getCode().equals(0))
                payer = Payer.BORROWER;
            else {
                if (payer == Payer.BORROWER)
                    payer = Payer.CO_BORROWER;
            }
        }
        return payer;
    }

    private void updateCard4Humo(Card c) {
        log.info("Updating Humo: {}", c.getMaskedPan());

        if (c.getIsConfirmed())
            return;

        c.setIsConfirmed(true);
        cardDAO.update(c);
    }


    private ResultData<VerifyResponseDTO> handleVerifyUzCard(VerifyRequestDTO requestDTO) {
        log.info("Starting verify uzCard method with request: {}", requestDTO);
        var result = addUzCardToDb(requestDTO);

        saveCardToLoanCard(result);

        VerifyResponseDTO verifyResponseDTO = new VerifyResponseDTO(result.getCard());

        log.info("Finished verify uzCard method with response dto: {}", verifyResponseDTO);
        return new ResultData<>(verifyResponseDTO);
    }

    @Override
    public CardConfirmResult addUzCardToDb(VerifyRequestDTO requestDTO) {
        var cardRedis = findCardByRedisId(requestDTO);

        Optional<Card> cardOptional;


        if (cardRedis.getCard().matches(CardUtils.regexUzCardToken())) {
            cardOptional = cardDAO.findCardByToken(cardRedis.getCard());
        } else
            cardOptional = cardDAO.findCardByPan(cardRedis.getCard());


        if (cardOptional.isEmpty())
            throw new EntityNotFoundException("Card: %s not found".formatted(
                    CardUtils.maskPanDigits(
                            CardUtils.decryptPan(cardRedis.getCard())
                    )
            ));

        var card = cardOptional.get();

        var response = verifyUzCard(requestDTO);

        if (response.getError() != null)
            throw new GeneralErrorException(response.getError().getMessage());

        updateCard4UzCard(card, response.getResult());

        return new CardConfirmResult(
                card,
                requestDTO,
                cardRedis.getLoanId()
        );
    }

    private Message findCardByRedisId(VerifyRequestDTO requestDTO) {
        var cardMessage = getMessageByIdFromRedis(requestDTO);

        if (cardMessage.getProcessing() != requestDTO.getType())
            throw new CardException("OTP and Verify card types must be the same!");

        if (requestDTO.getType() == Processing.HUMO)
            if (!Objects.equals(requestDTO.getCode(), cardMessage.getCode()))
                throw new InvalidDataException("Confirmation code not matched!");

        log.info("Card redis entity found with id: {}", cardMessage.getId());
        return cardMessage;
    }

    private Message findCardByRedisIdV2(VerifyRequestDTO requestDTO) {
        var cardMessage = getMessageByIdFromRedis(requestDTO);

        if (cardMessage.getProcessing() != requestDTO.getType())
            throw new CardException("OTP and Verify card types must be the same!");

        log.info("Card redis entity found with id: {}", cardMessage.getId());
        return cardMessage;
    }

    private UzCardBaseResponse<CardBaseResult> verifyUzCard(VerifyRequestDTO requestDTO) {
        return uzCardService.verify(new VerifyRequest(requestDTO.getId(), requestDTO.getCode()));
    }


    private void updateCard4UzCard(Card card, CardBaseResult response) {
        log.info("Updating card: {}", response.getPan());

        card.setHolder(response.getFullName());
        card.setPhone(response.getPhone());
        card.setMaskedPan(response.getPan());
        card.setIsConfirmed(true);
        card.setToken(response.getId());
        card.setIsActive(true);
        cardDAO.update(card);
    }

    private Message getMessageByIdFromRedis(VerifyRequestDTO requestDTO) {
        return messageRedisService.findById(requestDTO.getId());
    }


    private ResultData<OTPResponseDTO> getVisaDAVRResponse(OTPRequestDTO requestDTO, Loan loan) {
        log.info("Starting visa send otp method with request: {}", requestDTO);

        var preparedRequest = new VisaCardInfoRequest(
                requestDTO.getCard(),
                requestDTO.getExpiry(),
                "PAN"
        );

        var visaResponse = visaMiddlewareService.getCardInfo(
                preparedRequest
        );

        if (!visaResponse.getStatus())
            throw new GatewayException(visaResponse.getError().getMessage());

        var visaResult = visaResponse.getData();

        if (visaResult.getError() != null)
            throw new GatewayException(visaResponse.getData().getError().getMessage());


        String phone = visaResult.getResult().getPhone();

        if (phone == null || phone.isEmpty() || !phone.matches(CommonUtils.phoneRegex())) {
            log.error("Phone wrong format! value is: {}", phone);
            return new ResultData<>(ResultState.PhoneNotFound, ResultState.PhoneNotFound.getMessage());
        }

        if (!phone.equals(requestDTO.getPhone())) {
            log.error("Phones is incorrect! Requested phone: {}", phone);
            return new ResultData<>(ResultState.PhoneNotFound, ResultState.PhoneNotFound.getMessage());
        }


        cardDAO.create(visaResult.getResult(), false, Processing.DAVR_VISA);


        var code = SMSUtils.getRandomCode();

        var message = messageRedisService.createOTPV3(String.valueOf(System.currentTimeMillis()), code,
                phone,
                requestDTO.getCard(),
                Processing.DAVR_VISA,
                requestDTO.getLoanId(),
                loan.getExtId()
        );


        notificationComponent.sendCardConfirmNotification(
                loan.getExtId(),
                message.getCode(),
                requestDTO.getCard().substring(12),
                loan.getLanguage(),
                phone
        );


        ResultData<OTPResponseDTO> responseDTO = new ResultData<>(
                new OTPResponseDTO(
                        "******" + phone.substring(phone.length() - 4),
                        message.getId(),
                        Processing.DAVR_VISA,
                        false));

        log.info("Finished visa send otp method with response dto: {}", responseDTO);
        return responseDTO;
    }


    private ResultData<OTPResponseDTO> getHuMoOTPResponseV2(@NonNull OTPRequestDTO requestDTO, Loan loan) {
        log.info("Starting huMo card send otp v2 with request: {}", requestDTO);

        String phone = requestDTO.getPhone();

        var verifyRequest = new HuMoCardVerifyRequest(
                requestDTO.getCard(),
                "+" + phone
        );

        var verifyResponse = huMoClientService.verify(verifyRequest);

        if (!verifyResponse.getStatus())
            throw new GeneralErrorException(verifyResponse.getError().getMessage());

        if (!verifyResponse.getData().getIsSuccess())
            throw new GeneralErrorException(verifyResponse.getData().getError().getMessage());

        saveCard(requestDTO, Processing.HUMO);

        var message = messageRedisService.createOTPV3(
                String.valueOf(System.currentTimeMillis()),
                SMSUtils.getRandomCode(),
                phone,
                requestDTO.getCard(),
                Processing.HUMO,
                requestDTO.getLoanId(),
                loan.getExtId()
        );

        var responseDTO = ResultData.ok(
                new OTPResponseDTO(
                        "******" + phone.substring(phone.length() - 4),
                        message.getId(),
                        Processing.HUMO,
                        false)
        );

        log.info("Finished huMo card send otp method with response dto: {}", responseDTO);

        return responseDTO;
    }


    private ResultData<VerifyResponseDTO> handleVerifyHuMoV2(VerifyRequestDTO requestDTO) {
        log.info("Starting verify huMo v2 method with request: {}", requestDTO);

        var result = addHuMoCardToDB(requestDTO);

        saveCardToLoanCard(result);

        VerifyResponseDTO verifyResponseDTO = new VerifyResponseDTO(result.getCard());

        log.info("Finished verify huMo v2 method with response dto: {}", verifyResponseDTO);
        return new ResultData<>(verifyResponseDTO);
    }

    @Override
    public CardConfirmResult addHuMoCardToDB(VerifyRequestDTO requestDTO) {
        var cardRedis = findCardByRedisIdV2(requestDTO);

        var cardOptional = cardDAO.findCardByPan(cardRedis.getCard());

        if (cardOptional.isEmpty())
            throw new EntityNotFoundException(ResultState.CardNotFound.getMessage());

        var card = cardOptional.get();

        var confirmRequest = new HuMoCardConfirmRequest(
                CardUtils.decryptPan(cardRedis.getCard()),
                requestDTO.getCode()
        );

        var confirmResponse = huMoClientService.confirm(confirmRequest);

        if (!confirmResponse.getStatus())
            throw new GeneralErrorException(confirmResponse.getError().getMessage());

        if (!confirmResponse.getData().getIsSuccess())
            throw new GeneralErrorException(confirmResponse.getData().getError().getMessage());


        var cardInfoRequest = new HumoCardInfoRequest(
                CardUtils.decryptPan(cardRedis.getCard())
        );

        var cardInfoResponse = huMoClientService.getCardInfo(cardInfoRequest);


        if (cardInfoResponse.error() != null) {
            log.error(cardInfoResponse.error().getMessage());
            throw new GeneralErrorException(ResultState.PhoneNotFound.getMessage());
        }

        updateCard4HuMoV2(card, cardInfoResponse.result());

        return new CardConfirmResult(
                card,
                requestDTO,
                cardRedis.getLoanId()
        );
    }

    private void updateCard4HuMoV2(Card c, HumoCardInfoResponse.HumoCardResult result) {
        log.info("Updating huMo card: {}", c.getMaskedPan());

        String phone = null;

        if (result != null
                && result.mb() != null
                && result.mb().phone() != null)
            phone = improvePhone(result.mb().phone());


        if (result == null || result.card() == null)
            throw new GeneralErrorException("Card result or info is null, income data: %s".formatted(result));


        c.setExpiry(result.card().expiry());
        c.setIsActive(true);
        c.setPhone(phone);
        c.setMaskedPan(CardUtils.maskPanDigits(result.card().primaryAccountNumber()));
        c.setHolder(result.card().nameOnCard());

        var tokenResponse = getHumoTokenResponse(result.card().primaryAccountNumber());
        if (tokenResponse != null && tokenResponse.maskedPan() != null)
            c.setToken(tokenResponse.maskedPan());

        c.setIsConfirmed(true);
        cardDAO.update(c);
    }

    private HumoTokenResponse getHumoTokenResponse(String card) {
        log.info("Send token request for pan: {}", CardUtils.maskPanDigits(card));
        try {
            var response = huMoClientService.getCardToken(card);
            log.info("Token response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Token response has error: {}", e.getMessage());
            return null;
        }
    }

    private ResultData<OTPResponseDTO> getMasterOTPResponse(OTPRequestDTO requestDTO, Loan loan) {
        log.info("Starting MASTER send otp method with request: {}", requestDTO);
        var response = masterClientService.otpVerify(
                new MasterOTPVerifyRequest(
                        requestDTO.getCard(),
                        requestDTO.getExpiry(),
                        requestDTO.getPhone()
                )
        );

        if (response.getError() != null)
            return new ResultData<>(ResultState.NotProcessed, response.getError().getMessage());

        if (response.getData().getError() != null)
            return new ResultData<>(ResultState.EntityNotFound, response.getData().getError().getMessage());

        var result = response.getData().getResult();

        saveCard(requestDTO, Processing.DAVR_MASTER_CARD);

        saveUzCardOTPInRedis(
                requestDTO, String.valueOf(result.getId()),
                result.getPhone(), Processing.DAVR_MASTER_CARD,
                loan.getExtId()
        );

        ResultData<OTPResponseDTO> responseDTO = new ResultData<>(
                new OTPResponseDTO(
                        String.valueOf(result.getId()),
                        result.getPhone(),
                        Processing.DAVR_MASTER_CARD
                )
        );

        log.info("Finished MASTER send otp method with response dto: {}", responseDTO);
        return responseDTO;
    }

    private ResultData<VerifyResponseDTO> handleVerifyMaster(VerifyRequestDTO requestDTO) {
        log.info("Starting verify MASTER method with request: {}", requestDTO);
        var result = addMasterToDb(requestDTO, false);

        saveCardToLoanCard(result);

        VerifyResponseDTO verifyResponseDTO = new VerifyResponseDTO(result.getCard());

        log.info("Finished verify MASTER method with response dto: {}", verifyResponseDTO);
        return new ResultData<>(verifyResponseDTO);
    }

    @Override
    public CardConfirmResult addMasterToDb(VerifyRequestDTO requestDTO, Boolean isOneTimeCard) {
        var cardRedis = findCardByRedisId(requestDTO);

        Optional<Card> cardOptional;
        if (!isOneTimeCard)
            cardOptional = cardDAO.findCardByPan(cardRedis.getCard());
        else
            cardOptional = cardDAO.findCardByToken(cardRedis.getCard());

        if (cardOptional.isEmpty())
            throw new EntityNotFoundException("Card: %s not found".formatted(
                    cardRedis.getCard()
            ));

        var card = cardOptional.get();

        var response = masterClientService.otpConfirm(
                new MasterOTPConfirmRequest(
                        UUID.fromString(cardRedis.getId()),
                        requestDTO.getCode()
                )
        );

        if (response.getError() != null)
            throw new GeneralErrorException(response.getError().getMessage());

        if (response.getData().getError() != null)
            throw new GeneralErrorException(response.getData().getError().getMessage());

        var result = response.getData().getResult();

        updateCardAfterConfirm(card, result.getHolder(), result.getPhone(), result.getToken());

        return new CardConfirmResult(
                card,
                requestDTO,
                cardRedis.getLoanId()
        );
    }

    private void updateCardAfterConfirm(Card card, String fullName, String phone, String token) {
        log.info("Updating card: {}", card.getPan());

        card.setHolder(fullName);
        card.setPhone(phone);
        card.setIsConfirmed(true);
        card.setToken(token);
        card.setIsActive(true);
        cardDAO.update(card);
    }
}
