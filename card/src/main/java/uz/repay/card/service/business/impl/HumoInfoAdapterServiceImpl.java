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
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequest;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponse;
import uz.repay.card.service.business.HumoInfoAdapterService;
import uz.repay.card.service.web.HuMoClientService;
import uz.repay.card.service.web.HuMoGatewayService;
import uz.repay.common.exception.CardException;
import uz.repay.common.exception.GatewayException;
import uz.repay.common.exception.GeneralErrorException;
import uz.repay.common.exception.TimeOutException;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class HumoInfoAdapterServiceImpl implements HumoInfoAdapterService {
    private final HuMoClientService huMoClientService;
    private final ApplicationEventPublisher eventPublisher;
    private final HuMoGatewayService huMoGatewayService;

    @Override
    public List<LoanCardsResult> getCardsBalance(Map<Long, String> cardList) {
        var responseList = new ArrayList<LoanCardsResult>();
        log.info("Fetch {} HUMO cards balance", cardList.size());
        AtomicReference<Boolean> isExit = new AtomicReference<>(false);

        if (cardList.isEmpty())
            return responseList;


        cardList
                .forEach((key, value) -> {
                    var startTime = System.currentTimeMillis();

                    if (isExit.get())
                        return;

                    var result = getCardBalance(value);


                    if (result.getStatus()) {
                        if (result.getData().result() != null)
                            responseList.add(new LoanCardsResult(key, result));
                        else {
                            log.error("HuMo card: {} fetching error: {}", value, result);

                            setNotActiveCardAndSave(key, result);
                        }

                    } else {
                        if (result.getError().getCode().equals(510)) {
                            log.warn("Try to disable: {} processing after un success fetching cards info", Processing.HUMO);

                            isExit.set(true);


                            eventPublisher.publishEvent(
                                    new BacklashEventDTO(
                                            System.currentTimeMillis() - startTime,
                                            Processing.HUMO
                                    )
                            );
                        } else {
                            log.error("HuMo card: {} fetching error", value);
                        }
                    }

                });

        log.info("HUMO cards fetch balance finished");
        return responseList;
    }

    private void setNotActiveCardAndSave(Long key, SimpleResponse<HumoCardInfoResponse> result) {
        if (result.getData().error().getMessage().equals("Card not found")) {
            log.info("Card with id: {} returned \"Card not found\" message in response and will be set not active card", key);
            eventPublisher.publishEvent(new CardDeactivationDTO(key));
        }
    }


    private SimpleResponse<HumoCardInfoResponse> getCardBalance(String h) {
        try {
            var result = huMoClientService.getCardInfo(new HumoCardInfoRequest(h));
            return new SimpleResponse<>(result);
        } catch (TimeOutException t) {
            log.error("{} fetch card result has error: {}", h, t.getMessage());
            return new SimpleResponse<>(510, t.getMessage());
        } catch (Exception e) {
            log.error("{} fetch card result has error: {}", h, e.getMessage());
            return new SimpleResponse<>(500, e.getMessage());
        }
    }

    @Override
    public CardShortInfoDTO takeP2PInfo(@NonNull String pan) {
        log.info("Take huMo: {} p2p info", pan);
        var request = new HumoCardInfoRequest(
                pan
        );

        validateBinTypeAndPossibility(pan);

        var response = huMoClientService.p2pInfo(request);

        if (!response.getStatus())
            throw new GatewayException(response.getError().getMessage());

        if (response.getData().getError() != null)
            throw new GatewayException(response.getData().getError().getMessage());

        return new CardShortInfoDTO(
                response.getData().getResult()
        );

    }

    private void validateBinTypeAndPossibility(String pan) {
        var binsResult = huMoGatewayService.getBankBins();

        if (!binsResult.getStatus())
            throw new GatewayException(binsResult.getError().getMessage());

        if (binsResult.getData() == null || binsResult.getData().isEmpty())
            throw new GatewayException("Bins result is null or empty");

        if (binsResult
                .getData()
                .stream()
                .noneMatch(b -> pan.contains(b.getBinCode())))
            throw new GatewayException("Bin for card: %s not found in huMo bin list".formatted(pan));


        if (binsResult
                .getData()
                .stream()
                .noneMatch(z -> pan.contains(z.getBinCode()) && z.getIsPossible()))
            throw new CardException("Card: %s type is not PERSONAL".formatted(pan));
    }


    @Override
    public LoanCardsResult getOneCardBalance(String card) {
        var response = getCardBalance(card);

        if (!response.getStatus())
            throw new GeneralErrorException(response.getError().getMessage());

        if (response.getData().error() != null)
            throw new GatewayException(response.getData().error().getMessage());

        return new LoanCardsResult(null, response);
    }


}
