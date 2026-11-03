package uz.repay.card.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uz.repay.card.service.business.adapter.impl.detector.ABSCardDetectorImpl;
import uz.repay.card.service.business.adapter.impl.detector.AutoPayCardDetectorImpl;


@TestConfiguration
public class CardDetectorConfig {

    @Bean
    public ABSCardDetectorImpl absCardDetector() {
        ABSCardDetectorImpl mock = Mockito.mock(ABSCardDetectorImpl.class);
        Mockito.when(mock.isEnable()).thenReturn(true);
        return mock;
    }

    // Аналогично для остальных сервисов
    @Bean
    public AutoPayCardDetectorImpl autoPayCardDetector() {
        AutoPayCardDetectorImpl mock = Mockito.mock(AutoPayCardDetectorImpl.class);
        Mockito.when(mock.isEnable()).thenReturn(true);
        return mock;
    }
}
