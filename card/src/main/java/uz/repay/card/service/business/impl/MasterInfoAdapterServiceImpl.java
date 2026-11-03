package uz.repay.card.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.backlash.BacklashEventDTO;
import uz.repay.card.model.dto.backlash.CardDeactivationDTO;
import uz.repay.card.model.dto.core.response.card.CardShortInfoDTO;
import uz.repay.card.model.dto.core.response.card.result.LoanCardsResult;
import uz.repay.card.model.dto.master.request.MasterBalanceRequest;
import uz.repay.card.model.dto.master.request.MasterP2PInfoRequest;
import uz.repay.card.service.business.MasterInfoAdapterService;
import uz.repay.card.service.web.MasterClientService;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.model.enums.Processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterInfoAdapterServiceImpl implements MasterInfoAdapterService {
    private final MasterClientService masterClientService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<LoanCardsResult> getCardsBalance(Map<Long, String> cardList) {
        var responseList = new ArrayList<LoanCardsResult>();
        log.info("Fetch {} Master cards balance", cardList.size());
        AtomicReference<Boolean> isExit = new AtomicReference<>(false);

        if (cardList.isEmpty())
            return responseList;


        cardList
                .forEach((key, value) -> {
                    var startTime = System.currentTimeMillis();

                    if (isExit.get())
                        return;

                    var result = masterClientService.balance(
                            new MasterBalanceRequest(value)
                    );


                    if (!result.getStatus()) {
                        if (result.getError().getCode().equals(580)) {
                            log.warn("Try to disable: {} processing after un success fetching cards info", Processing.HUMO);

                            isExit.set(true);


                            eventPublisher.publishEvent(
                                    new BacklashEventDTO(
                                            System.currentTimeMillis() - startTime,
                                            Processing.DAVR_MASTER_CARD
                                    )
                            );
                        } else {
                            log.error("Master card: {} fetching error", value);
                        }
                        return;
                    }

                    if (result.getData().getError() != null) {
                        log.error("Master card: {} fetching error", value);

                        if (result.getData().getError().getCode().equals(1026)) // 1026 = Card not found
                            setNotActiveCardAndSave(key);

                        return;
                    }

                    responseList.add(new LoanCardsResult(key, result.getData().getResult()));

                });

        log.info("MASTER cards fetch balance finished");
        return responseList;
    }

    @Override
    public LoanCardsResult getOneCardBalance(String card) {
        var response = masterClientService.balance(
                new MasterBalanceRequest(card)
        );

        if (!response.getStatus())
            throw new GeneralErrorException(response.getError().getMessage());

        if (response.getData().getError() != null)
            throw new GatewayException(response.getData().getError().getMessage());

        return new LoanCardsResult(null, response.getData().getResult());
    }

    @Override
    public CardShortInfoDTO takeP2PInfo(@NonNull String pan) {
        log.info("Take MASTER: {} p2p info", pan);


        var response = masterClientService.p2pInfo(
                new MasterP2PInfoRequest(pan)
        );

        if (!response.getStatus())
            throw new GatewayException(response.getError().getMessage());

        if (response.getData().getError() != null)
            throw new GatewayException(response.getData().getError().getMessage());

        var result = new CardShortInfoDTO(
                response.getData().getResult()
        );

        log.info("Ready MASTER p2pInfo with response: {}", result);
        return result;
    }

    private void setNotActiveCardAndSave(Long key) {
        log.info("Card with id: {} returned \"Card not found\" message in response and will be set not active card", key);
        eventPublisher.publishEvent(new CardDeactivationDTO(key));
    }

}
