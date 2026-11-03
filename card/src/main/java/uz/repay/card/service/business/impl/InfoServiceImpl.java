package uz.repay.card.service.business.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.core.request.card.GetCardByTokenRequest;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.GetCardByTokenResponse;
import uz.repay.card.model.entity.Card;
import uz.repay.card.model.enums.PC;
import uz.repay.card.service.business.InfoService;
import uz.repay.card.service.core.CardDAO;
import uz.repay.common.exception.CardException;
import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;
import uz.repay.common.model.dto.request.card.response.CardFullInfoResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.utils.CardCommon;
import uz.repay.common.utils.CardUtils;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {
    private final CardDAO cardDAO;
    private final CardCommon cardCommon;
    private final ObjectMapper objectMapper;

    @Override
    public ResultData<CardFullInfoResponseDTO> getCardByPan(CardFullInfoRequestDTO requestDTO) {
        log.info("Get card full info by pan: {}", requestDTO);

        var result = cardDAO.findCardByPan(requestDTO.getPan())
                .orElseThrow(() -> new CardException("Card not found"));

        return ResultData.ok(
                new CardFullInfoResponseDTO(
                        result.getId(), result.getMaskedPan(),
                        result.getHolder(), result.getProcessing(),
                        result.getPhone(), result.getIsConfirmed(),
                        result.getToken(), result.getService(),
                        result.getIsActive(), result.getExpiry(),
                        result.getInitTime(), result.getCreatedBy(),
                        result.getUpdateTime())
        );
    }

    @Override
    public ResultData<Boolean> deActivateCard(Long id) {
        log.info("Deactivate card by id: {}", id);

        var card = cardDAO.findById(id);

        card.setIsActive(false);
        cardDAO.update(card);

        log.info("Card: {} deactivated", card.getPan());

        return ResultData.ok(
                true
        );
    }


    @Override
    public ResultData<CardShortInfoDTO> takeP2PInfo(@NotNull String pan) {
        var pc = cardCommon.detectCardProcessing(pan);

        switch (pc) {
            case UZCARD -> {
                var service = PC.UZCARD.cardService();
                return ResultData.ok(
                        service.takeP2PInfo(pan)
                );
            }
            case HUMO -> {
                var service = PC.HUMO.cardService();
                return ResultData.ok(
                        service.takeP2PInfo(pan)
                );
            }
            case DAVR_VISA -> {
                var service = PC.DAVR_VISA.cardService();
                return ResultData.ok(
                        service.takeP2PInfo(pan)
                );
            }
            case DAVR_MASTER_CARD -> {
                var service = PC.DAVR_MASTER_CARD.cardService();
                return ResultData.ok(
                        service.takeP2PInfo(pan)
                );
            }
            default -> {
                return new ResultData<>(-1001, "Not found p2p info method for pc: %s".formatted(pc));
            }
        }
    }

    @Override
    public GetCardByTokenResponse getCardByToken(GetCardByTokenRequest requestDTO) {
        log.info("PAN_REQUEST_TO_DB: {}",requestDTO.getToken());
        Optional<Card> card = cardDAO.findCardByToken(requestDTO.getToken());
        if (card.isPresent()) {
            try {
                log.info("CARD_PAN_MAKED1: {}",card.get().getPan());
                log.info("CARD_PAN_MAKED2: {}",card.get().getPan());
                log.info("CARD_PAN_MAKED3: {}",CardUtils.decryptPan(card.get().getPan()));
                log.info(" GET_CARD_PAN_BY_TOKEN: {}", objectMapper.writeValueAsString(card));
            } catch (JsonProcessingException e) {
                log.info(" Object writing error: {}", e.getMessage());
            }
            return new GetCardByTokenResponse(CardUtils.decryptPan(card.get().getPan()));
        }
        log.info("CARD_NOT_FOUND_WITH_MASKED_PAN: {}",requestDTO.getToken());
        return new GetCardByTokenResponse(null);
    }
}
