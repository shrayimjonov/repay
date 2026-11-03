package uz.repay.card.model.enums;

import lombok.Getter;
import uz.repay.card.service.business.*;
import uz.repay.common.service.ApplicationContextProvider;

@Getter
public enum PC {
    UZCARD {
        @Override
        public CardAdapterService cardService() {
            return ApplicationContextProvider.getBean(UzCardInfoAdapterService.class);
        }
    },
    HUMO {
        @Override
        public CardAdapterService cardService() {
            return ApplicationContextProvider.getBean(HumoInfoAdapterService.class);
        }
    },

    DAVR_VISA {
        @Override
        public CardAdapterService cardService() {
            return ApplicationContextProvider.getBean(VisaDavrInfoAdapterService.class);
        }
    },
    AUTOPAY {
        @Override
        public CardAdapterService cardService() {
            return ApplicationContextProvider.getBean(AutoPayInfoAdapterService.class);
        }
    },
    DAVR_MASTER_CARD {
        @Override
        public CardAdapterService cardService() {
            return ApplicationContextProvider.getBean(MasterInfoAdapterService.class);
        }
    };

    public abstract CardAdapterService cardService();
}
