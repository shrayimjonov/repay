package uz.nodir.scoring.service.core.bean.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.nodir.scoring.service.business.limits.CardLimitAdapter;
import uz.nodir.scoring.service.core.bean.LimitServiceRegistrar;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class LimitServiceRegistrarImpl implements LimitServiceRegistrar {

    private final Map<Processing, CardLimitAdapter> cardLimitAdapterMap = new HashMap<>();

    @Autowired
    public LimitServiceRegistrarImpl(List<CardLimitAdapter> cardLimitAdapters) {
        registerCardInfoAdapterV2Interfaces(cardLimitAdapters);
    }

    private void registerCardInfoAdapterV2Interfaces(List<CardLimitAdapter> cardAdapterV2Services) {
        for (var service : cardAdapterV2Services) {
            var pc = service.getPC();

            log.info("Card limit adapter v1 bean registered for processing: {}", pc);

            cardLimitAdapterMap.put(pc, service);
        }
    }

    @Override
    public SimpleResponse<CardLimitAdapter> getCardLimitAdapterByPC(Processing pc) {
        log.info("Get card limit adapter v1 for processing: {}", pc);

        var service = cardLimitAdapterMap.get(pc);

        if (service == null) {
            var message = "Card limit adapter not found for processing: %s".formatted(pc);
            log.error(message);
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), message);
        }

        return new SimpleResponse<>(service);
    }

}
