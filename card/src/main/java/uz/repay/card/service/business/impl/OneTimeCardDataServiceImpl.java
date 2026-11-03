package uz.repay.card.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.OTPRequestDTO;
import uz.repay.card.model.dto.core.request.card.VerifyRequestDTO;
import uz.repay.card.model.dto.core.response.card.OTPResponseDTO;
import uz.repay.card.model.dto.core.response.card.result.CardConfirmResult;
import uz.repay.card.model.dto.core.response.card.result.CardFamiliarResult;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.model.dto.master.request.MasterBalanceRequest;
import uz.repay.card.model.dto.master.request.MasterP2PInfoRequest;
import uz.repay.card.model.dto.master.response.MasterBalanceResponse;
import uz.repay.card.model.dto.uzcard.request.BalanceRequest;
import uz.repay.card.model.dto.uzcard.request.P2PInfoRequest;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.CardBaseResult;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.enums.PC;
import uz.repay.card.redis.entity.Message;
import uz.repay.card.service.business.OneTimeCardDataService;
import uz.repay.card.service.business.VerifyCardService;
import uz.repay.card.service.business.component.NotificationComponent;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.service.core.LoanDAO;
import uz.repay.card.service.core.MessageRedisService;
import uz.repay.card.service.web.HuMoClientService;
import uz.repay.card.service.web.MasterClientService;
import uz.repay.card.service.web.UzCardService;
import uz.repay.card.util.SMSUtils;
import uz.repay.common.exception.*;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CardCommon;
import uz.repay.common.utils.CardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static uz.repay.card.util.ServiceUtils.improvePhone;

@Service
@Slf4j
@RequiredArgsConstructor
public class OneTimeCardDataServiceImpl implements OneTimeCardDataService {
    private final VerifyCardService verifyCardService;
    private final UzCardService uzCardService;
    private final CardCommon cardCommon;
    private final HuMoClientService huMoClientService;
    private final CardDAO cardDAO;
    private final MessageRedisService messageRedisService;
    private final NotificationComponent notificationComponent;
    private final LoanDAO loanDAO;
    private final MasterClientService masterClientService;

    @Override
    public ResultData<OTPResponseDTO> verify(@NotNull OTPRequestDTO requestDTO) {
        log.info("Starting verify card request: {}", requestDTO);
        var loan = loanDAO.findById(requestDTO.getLoanId());

        var pc = cardCommon.detectCardProcessing(requestDTO.getCard());

        var cardInfoResult = cardIsAlreadyAdded(requestDTO.getCard(), pc);

        if (cardInfoResult.getIsFamiliar()) {

            String phone = cardInfoResult.getPhone();

            if (phone == null) {
                log.error("Phone wrong format!");
                return new ResultData<>(ResultState.PhoneNotFound, ResultState.PhoneNotFound.getMessage());
            }

            Optional<Card> cardOptional;

            if (pc == Processing.HUMO || pc == Processing.DAVR_VISA)
                cardOptional = cardDAO.findCardByPan(
                        CardUtils.encryptPan(
                                requestDTO.getCard()
                        )
                );
            else if (pc == Processing.UZCARD || pc == Processing.DAVR_MASTER_CARD) {
                if (cardInfoResult.getResult() instanceof CardBaseResult cardResult) {
                    cardOptional = cardDAO.findCardByToken(cardResult.getId());
                } else if (cardInfoResult.getResult() instanceof MasterBalanceResponse cardResult) {
                    cardOptional = cardDAO.findCardByToken(cardResult.getToken());
                } else
                    throw new CardException("Card result: %s find method in dao not detected!".formatted(cardInfoResult));

            } else {
                throw new CardException("Card result: %s not configured!".formatted(cardInfoResult));
            }

            Card card;

            if (cardOptional.isEmpty()) {
                log.info("Card optional result is: false");
                if (cardInfoResult.getResult() instanceof HumoCardInfoResponse) {
                    card = cardDAO.create((HumoCardInfoResponse) cardInfoResult.getResult(), false);
                } else if (cardInfoResult.getResult() instanceof CardBaseResult) {
                    card = cardDAO.create((CardBaseResult) cardInfoResult.getResult(), false, pc);
                } else if (cardInfoResult.getResult() instanceof MasterBalanceResponse) {
                    card = cardDAO.create((MasterBalanceResponse) cardInfoResult.getResult(), false, pc);
                } else
                    throw new GeneralErrorException("Card DAO save method not configured");
            } else {
                card = cardOptional.get();
                log.info("Card optional result is: {}", card);

                if (cardInfoResult.getResult() instanceof HumoCardInfoResponse response) {
                    card.setHolder(response.result().card().nameOnCard());
                    card.setExpiry(response.result().card().expiry());
                    if (response.result().mb() != null && response.result().mb().state().equals("on"))
                        card.setPhone(response.result().mb().phone());

                    cardDAO.update(card);

                } else if (cardInfoResult.getResult() instanceof CardBaseResult response) {
                    card.setHolder(response.getFullName());
                    if (response.getPhone() != null && !response.getPhone().isEmpty())
                        card.setPhone(response.getPhone());

                    cardDAO.update(card);

                } else if (cardInfoResult.getResult() instanceof MasterBalanceResponse response) {
                    card.setHolder(response.getHolder());
                    if (response.getPhone() != null && !response.getPhone().isEmpty())
                        card.setPhone(response.getPhone());

                    cardDAO.update(card);
                } else
                    throw new GeneralErrorException("Card DAO save method not configured");
            }

            var saveCard = pc.equals(Processing.HUMO) || pc.equals(Processing.DAVR_VISA) ? card.getPan() : card.getToken();

            var message = messageRedisService.createOTPV2(
                    String.valueOf(System.currentTimeMillis()),
                    SMSUtils.getRandomCode(),
                    phone,
                    saveCard,
                    pc,
                    requestDTO.getLoanId()
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
                            pc,
                            true));

            log.info("Finished verify card method with response dto: {}", responseDTO);
            return responseDTO;
        } else {
            return verifyCardService.sendOTP(requestDTO);
        }
    }

    private CardFamiliarResult<?> cardIsAlreadyAdded(String pan, Processing pc) {
        log.info("Check card: {} to confirmed", pan);


        switch (pc) {
            case UZCARD -> {
                return checkUzCard2Familiar(pan);
            }
            case HUMO -> {
                return checkHuMo2Familiar(pan);
            }
            case DAVR_VISA -> {
                log.info("DAVR Visa card always need confirmation for pan: {}", pan);
                return new CardFamiliarResult<>(
                        false,
                        null
                );
            }
            case DAVR_MASTER_CARD -> {
                return checkMasterFamiliar(pan);
            }
            default -> throw new GatewayException("Card checker to familiar for pc: %s not configured!".formatted(pc));
        }

    }


    private CardFamiliarResult<HumoCardInfoResponse> checkHuMo2Familiar(String pan) {
        var huMoInfoResponse = huMoClientService.getCardInfo(
                new HumoCardInfoRequest(pan)
        );

        if (huMoInfoResponse.error() != null) {

            if (!huMoInfoResponse.error().getCode().equals(403)) {
                throw new GatewayException(huMoInfoResponse.error().getMessage());
            }

            log.info("HuMo throws error: {} >> Need card confirmation", huMoInfoResponse.error());
            return new CardFamiliarResult<>(
                    false,
                    null
            );
        }

        log.info("Card: {} is familiar, holder: {}", pan, huMoInfoResponse.result().card().nameOnCard());

        var result = huMoInfoResponse.result();

        if (!result.mb().state().equals("on"))
            throw new GeneralErrorException("Card: %s phone is not linked".formatted(pan));

        var phone = improvePhone(result.mb().phone());

        return new CardFamiliarResult<>(
                true,
                phone,
                huMoInfoResponse
        );
    }

    private CardFamiliarResult<CardBaseResult> checkUzCard2Familiar(String pan) {
        var p2pInfoResponse = uzCardService.getP2PInfo(
                new P2PInfoRequest(pan)
        );

        if (p2pInfoResponse.getError() != null)
            throw new GeneralErrorException(p2pInfoResponse.getError().getMessage());

        var tokenResponse = getUzCardInfo(p2pInfoResponse.getResult().getId());

        if (tokenResponse.getError() != null) {
            log.info("Card: {} is not familiar... Need confirmation first time", pan);
            return new CardFamiliarResult<>(
                    false,
                    null
            );
        }


        log.info("Card: {} is familiar with token: {}", pan, p2pInfoResponse.getResult().getId());


        var result = tokenResponse.getResult().getFirst();

        if (!result.getSms())
            throw new GeneralErrorException("Card: %s phone is not linked".formatted(pan));

        String phone = result.getPhone();

        if (phone.startsWith("000"))
            phone = "998" + phone.substring(3);


        return new CardFamiliarResult<>(
                true,
                phone,
                result
        );
    }

    private UzCardBaseResponse<List<CardBaseResult>> getUzCardInfo(String token) {
        try {
            return uzCardService.getCardsInfo(
                    new BalanceRequest(
                            new ArrayList<>(List.of(token))
                    )
            );
        } catch (Exception e) {
            throw new GatewayException(e.getMessage());
        }
    }

    @Override
    public ResultData<LoanCardsResult> confirmNotFamiliar(@NotNull VerifyRequestDTO requestDTO) {
        log.info("Starting confirm not familiar card request: {}", requestDTO);
        CardConfirmResult response;
        switch (requestDTO.getType()) {
            case UZCARD -> response = verifyCardService.addUzCardToDb(requestDTO);
            case HUMO -> response = verifyCardService.addHuMoCardToDB(requestDTO);
            case DAVR_VISA -> response = verifyCardService.addVisaToDB(requestDTO);
            case DAVR_MASTER_CARD -> response = verifyCardService.addMasterToDb(requestDTO, true);
            default ->
                    throw new GeneralErrorException("Confirm method for pc: %s not configured!".formatted(requestDTO.getType()));
        }

        LoanCardsResult cardInfo = getCardInfo(requestDTO.getType(), response.getCard());

        var responseDTO = ResultData.ok(cardInfo);
        log.info("Finished confirm card method with response dto: {}", responseDTO);

        return responseDTO;
    }

    private static LoanCardsResult getCardInfo(Processing pc, Card card) {
        LoanCardsResult cardInfo;
        switch (pc) {
            case UZCARD -> {
                var service = PC.UZCARD.cardService();
                cardInfo = service.getOneCardBalance(card.getToken());
            }
            case HUMO -> {
                var service = PC.HUMO.cardService();
                cardInfo = service.getOneCardBalance(CardUtils.decryptPan(card.getPan()));
            }
            case DAVR_VISA -> {
                var service = PC.DAVR_VISA.cardService();
                cardInfo = service.getOneCardBalance(CardUtils.decryptPan(card.getPan()));
            }
            case DAVR_MASTER_CARD -> {
                var service = PC.DAVR_MASTER_CARD.cardService();
                cardInfo = service.getOneCardBalance(card.getToken());
            }
            default -> throw new GeneralErrorException("Get balance method for pc: %s not configured!".formatted(pc));

        }
        return cardInfo;
    }

    @Override
    public ResultData<LoanCardsResult> confirmFamiliar(VerifyRequestDTO requestDTO) {
        log.info("Starting confirm familiar card request: {}", requestDTO);

        var cardRedis = checkMessageFromRedis(requestDTO);
        Optional<Card> cardOptional;

        if (cardRedis.getCard().matches(CardUtils.regexUzCardToken())) {
            cardOptional = cardDAO.findCardByToken(cardRedis.getCard());
        } else
            cardOptional = cardDAO.findCardByPanV2(cardRedis.getCard());


        if (cardOptional.isEmpty())
            throw new EntityNotFoundException(ResultState.CardNotFound.getMessage());

        var card = cardOptional.get();

        LoanCardsResult cardInfo = getCardInfo(requestDTO.getType(), card);

        var responseDTO = ResultData.ok(
                cardInfo
        );

        log.info("Finished confirm familiar card response: {}", responseDTO);
        return responseDTO;
    }

    private Message checkMessageFromRedis(VerifyRequestDTO requestDTO) {
        var cardMessage = messageRedisService.findById(requestDTO.getId());


        if (cardMessage.getProcessing() != requestDTO.getType())
            throw new CardException("OTP and Verify card types must be the same!");


        if (!Objects.equals(requestDTO.getCode(), cardMessage.getCode()))
            throw new InvalidDataException("Confirmation code not matched or its not familiar card!");

        log.info("Card redis entity found with id: {}", cardMessage.getId());
        return cardMessage;
    }

    private CardFamiliarResult<MasterBalanceResponse> checkMasterFamiliar(String pan) {
        var p2pInfoResponse = masterClientService.p2pInfo(
                new MasterP2PInfoRequest(pan)
        );

        if (p2pInfoResponse.getError() != null)
            throw new GeneralErrorException(p2pInfoResponse.getError().getMessage());

        if (p2pInfoResponse.getData().getError() != null)
            throw new GeneralErrorException(p2pInfoResponse.getData().getError().getMessage());

        String token = p2pInfoResponse.getData().getResult().getToken();

        var tokenResponse = masterClientService.balance(
                new MasterBalanceRequest(
                        token
                )
        );

        if (tokenResponse.getError() != null)
            throw new GeneralErrorException(tokenResponse.getError().getMessage());


        if (tokenResponse.getData().getError() != null) {
            log.info("Card: {} is not familiar... Need confirmation first time", pan);
            return new CardFamiliarResult<>(
                    false,
                    null
            );
        }


        log.info("Card: {} is familiar with token: {}", pan, token);


        var result = tokenResponse.getData().getResult();

        if (result.getPhone() == null || result.getPhone().isEmpty())
            throw new GeneralErrorException("Card: %s phone is not linked".formatted(pan));

        String phone = result.getPhone();

        if (phone.startsWith("000"))
            phone = "998" + phone.substring(3);


        return new CardFamiliarResult<>(
                true,
                phone,
                result
        );
    }
}
