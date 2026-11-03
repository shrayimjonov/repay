package uz.repay.card.service.business.bean.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uz.repay.card.service.business.adapter.CardAdapterV2Service;
import uz.repay.card.service.business.adapter.CardDetectorService;
import uz.repay.card.service.business.adapter.CardSaveService;
import uz.repay.card.service.business.bean.CardBeanRegistrar;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CardBeanRegistrarImpl implements CardBeanRegistrar {
    private final Map<Processing, CardAdapterV2Service> cardInfoAdapterList = new HashMap<>();
    private final Map<Processing, CardSaveService> cardSaveServiceMap = new HashMap<>();
    private final List<CardDetectorService> cardDetectorServiceList = new ArrayList<>();

    @Autowired
    public CardBeanRegistrarImpl(List<CardAdapterV2Service> cardAdapterV2Services,
                                 List<CardSaveService> cardSaveServiceList,
                                 List<CardDetectorService> cardDetectorServiceList) {
        registerCardInfoAdapterV2Interfaces(cardAdapterV2Services);
        registerCardSaveInterfaces(cardSaveServiceList);
        registerCardDetectorInterfaceImpls(cardDetectorServiceList);
    }

    private void registerCardInfoAdapterV2Interfaces(List<CardAdapterV2Service> cardAdapterV2Services) {
        for (var service : cardAdapterV2Services) {
            var processingResult = service.getPC();

            if (!processingResult.getStatus()) {
                log.error("Register bean: {} has error! Cause: {}", service, processingResult.getError().getMessage());
                continue;
            }

            Processing pc = service.getPC().getData();
            log.info("Card adapter v2 bean registered for processing: {}", pc);

            cardInfoAdapterList.put(pc, service);
        }
    }

    private void registerCardSaveInterfaces(List<CardSaveService> cardSaveServiceList) {
        for (var service : cardSaveServiceList) {
            var processingResult = service.getPC();

            if (!processingResult.getStatus()) {
                log.error("Register bean: {} has error! Cause: {}", service, processingResult.getError().getMessage());
                continue;
            }

            Processing pc = service.getPC().getData();
            log.info("Card save service bean registered for processing: {}", pc);

            cardSaveServiceMap.put(pc, service);
        }
    }

    private void registerCardDetectorInterfaceImpls(List<CardDetectorService> cardDetectorServices) {
        log.info("Detected CardDetectorService beans: {}", cardDetectorServices);

        for (var service : cardDetectorServices) {

            if (!service.isEnable()) {
                log.warn("Card detector service: {} is disabled and will not be registered.", service.getClass().getSimpleName());
                continue;
            }

            log.info("Card detector service bean registered with name: {}", service.getClass().getSimpleName());
            cardDetectorServiceList.add(service);
        }
    }

    @Override
    public SimpleResponse<CardAdapterV2Service> getCardInfoV2AdapterByPC(Processing pc) {
        log.info("Get card info adapter v2 for processing: {}", pc);

        var service = cardInfoAdapterList.get(pc);

        if (service == null) {
            var message = "Card info adapter not found for processing: %s".formatted(pc);
            log.error(message);
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), message);
        }

        return new SimpleResponse<>(service);
    }

    @Override
    public SimpleResponse<CardSaveService> getCardSaveServiceByPC(Processing pc) {
        log.info("Get card save service for processing: {}", pc);

        var service = cardSaveServiceMap.get(pc);

        if (service == null) {
            var message = "Card save service not found for processing: %s".formatted(pc);
            log.error(message);
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), message);
        }

        return new SimpleResponse<>(service);
    }

    @Override
    public List<CardDetectorService> cardDetectorServices() {
        log.info("Get all enabled card detector services");
        return cardDetectorServiceList;
    }


}
