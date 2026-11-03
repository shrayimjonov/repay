package uz.repay.card.service.business.bean;

import uz.repay.card.model.enums.PC;
import uz.repay.card.service.business.adapter.CardAdapterV2Service;
import uz.repay.card.service.business.adapter.CardDetectorService;
import uz.repay.card.service.business.adapter.CardSaveService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;

import java.util.List;

public interface CardBeanRegistrar {
    SimpleResponse<CardAdapterV2Service> getCardInfoV2AdapterByPC(Processing pc);
    SimpleResponse<CardSaveService> getCardSaveServiceByPC(Processing pc);
    List<CardDetectorService> cardDetectorServices();

}
