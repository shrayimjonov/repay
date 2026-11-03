package uz.repay.card.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.config.aop.CardFounder;
import uz.repay.card.model.dto.core.request.card.GuarantorCardInfoRequestDTO;
import uz.repay.card.model.dto.core.request.guarantor.GuarantorCardFinderRequestDTO;
import uz.repay.card.model.dto.core.request.loan.GuarantorCardToggleRequestDTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.core.response.card.CardInfoResponseDTO;
import uz.repay.card.service.business.GuarantorCardService;
import uz.repay.card.service.business.component.LogProtocolComponent;
import uz.repay.card.service.core.GuarantorCardDAO;
import uz.repay.card.service.core.GuarantorDAO;
import uz.repay.card.service.core.component.MasterCardAdderComponent;
import uz.repay.card.service.core.component.UzCardAdderComponent;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ProtocolType;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;

import java.util.ArrayList;


@Service
@Slf4j
@RequiredArgsConstructor
public class GuarantorCardServiceImpl implements GuarantorCardService {
    private final GuarantorDAO guarantorDAO;
    private final GuarantorCardDAO dao;
    private final LogProtocolComponent logProtocolComponent;
    private final UzCardAdderComponent uzCardAdderComponent;
    private final MasterCardAdderComponent masterCardAdderComponent;

    @Override
    public ResultData<CardInfoResponseDTO> getCardInfo(@NonNull GuarantorCardInfoRequestDTO requestDTO) {
        log.info("Received command to get guarantor card info with request dto: {}", requestDTO);

        var guarantorCard = dao.findByGuarantorAndCard(requestDTO.getGuarantorId(), requestDTO.getCardId());

        var result = guarantorCard
                .map(card -> new ResultData<>(new CardInfoResponseDTO(card)))
                .orElseGet(
                        () -> new ResultData<>(
                                ResultState.EntityNotFound,
                                "GuarantorId: %d with cardId: %d not found in database".formatted(
                                        requestDTO.getGuarantorId(),
                                        requestDTO.getCardId()
                                )
                        )
                );

        log.info("Finished command to get guarantor card info with response dto: {}", result);
        return result;
    }

    @Override
    public ResultData<CoreError> deleteOrApproveCard(GuarantorCardToggleRequestDTO requestDTO) {
        log.info("Received command to delete/approve card from guarantor with request dto: {}", requestDTO);
        Long cardId = requestDTO.getCardId();
        Long guarantorId = requestDTO.getGuarantorId();

        var loanCardOptional = dao.findByGuarantorAndCard(guarantorId, cardId);

        if (loanCardOptional.isEmpty()) {
            var message = "GuarantorCard with guarantorId: %d and cardId: %d not found".formatted(guarantorId, cardId);
            log.error(message);
            return new ResultData<>(ResultState.EntityNotFound, message);
        }

        var guarantorCard = loanCardOptional.get();

        guarantorCard.setIsDeleted(!guarantorCard.getIsDeleted());
        dao.save(guarantorCard);

        logProtocolComponent.saveLog(
                requestDTO.getLoanExternalId(),
                ProtocolType.CARD_GUARANTOR_CARD_TOGGLE,
                requestDTO.toMap()
        );

        var result = new ResultData<>(new CoreError(0, ResponseStatus.SUCCESS.name()));

        log.info("Finished command to delete/approve card from guarantor with response dto: {}", result);
        return result;
    }


    @Override
    @SuppressWarnings("all")
    @CardFounder
    public ResultData<CardFinderResponseDTO> findGuarantorCards(@NotNull GuarantorCardFinderRequestDTO requestDTO) {
        log.info("Received command to find cards by guarantor with request dto: {}", requestDTO);

        var guarantorOptional = guarantorDAO.findById(requestDTO.getGuarantorId());

        if (guarantorOptional.isEmpty()) {
            var message = "Guarantor with id: %d not found".formatted(requestDTO.getGuarantorId());
            return new ResultData<>(ResultState.EntityNotFound, message);
        }

        var guarantor = guarantorOptional.get();


        var newCards = new ArrayList<String>();

        var cardsResponseDTO = new CardFinderResponseDTO();

//        log.info("<<< Starting huMo adder >>>");
//        var huMoAdder = humoCardAdderComponent.getCardFinderResponse(loan);
//        newCards.addAll(huMoAdder.getResult().getCards());

        var uzCardAdder = uzCardAdderComponent.getGuarantorAutoPayCardsV1(
                guarantor,
                requestDTO.getLoanExtId(),
                cardsResponseDTO

        );
        newCards.addAll(uzCardAdder.getResult().getCards());

        var masterAdder = masterCardAdderComponent.getGuarantorCardFinderResponse(guarantor);
        newCards.addAll(masterAdder.getResult().getCards());

        var resultData = ResultData.ok(
                new CardFinderResponseDTO(newCards)
        );

        log.info("Finished command to find cards by guarantor with response dto: {}", resultData);
        return resultData;
    }
}
