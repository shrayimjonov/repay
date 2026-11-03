package uz.repay.card.service.business.adapter.detector;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uz.repay.card.service.business.adapter.CardDetectorService;
import uz.repay.card.service.business.adapter.impl.detector.*;
import uz.repay.card.service.business.bean.CardBeanRegistrar;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest
public class CardDetectorServiceTest {

    @Autowired
    private CardBeanRegistrar cardBeanRegistrar;

    @Test
    public void detectorServices() {
        log.info("Start checking detector services to not empty");

        var cardDetectorServiceList = cardBeanRegistrar.cardDetectorServices();
        log.info("Card detector services found: {}", cardDetectorServiceList);

        assertFalse(cardDetectorServiceList.isEmpty());
        log.info("Finish checking detector services");
    }

    @Test
    public void checkFreeCardDetectorServices() {
        log.info("Start checking free card detector services");

        var usePaidMethods = false;
        log.info("use paid methods value is: {}", usePaidMethods);

        var foundDetectors = takeCardDetectorServices(usePaidMethods);
        log.info("Card detector free services found: {}", foundDetectors);

        assertEquals(
                foundDetectors,
                List.of(
                        ABSCardDetectorImpl.class,
                        DavrMobile1CardDetectorImpl.class,
                        DavrMobile2CardDetectorImpl.class,
                        MasterCardDetectorImpl.class,
                        VisaCardDetectorImpl.class
                )
        );

        log.info("Finish checking free card detector services");
    }


    @Test
    public void checkAllCardDetectorServices() {
        log.info("Start checking all card detector services");

        var usePaidMethods = true;
        log.info("use paid methods value is: {}", usePaidMethods);

        var foundDetectors = takeCardDetectorServices(usePaidMethods);
        log.info("Card detector all services found: {}", foundDetectors);

        assertEquals(foundDetectors, List.of(
                ABSCardDetectorImpl.class,
                AutoPayCardDetectorImpl.class,
                DavrMobile1CardDetectorImpl.class,
                DavrMobile2CardDetectorImpl.class,
                HuMoCardDetectorImpl.class,
                MasterCardDetectorImpl.class,
                VisaCardDetectorImpl.class
        ));
        log.info("Finish checking all card detector services");
    }

    private List<? extends Class<? extends CardDetectorService>> takeCardDetectorServices(boolean usePaidMethods) {
        var cardDetectorServiceList = cardBeanRegistrar.cardDetectorServices();

        return cardDetectorServiceList
                .stream()
                .filter(z -> {
                    log.info("Check card detector service: {} to using paid method: {}", z.getClass(), usePaidMethods);
                    if (!usePaidMethods) {
                        return !z.isPaid();
                    }
                    return true;
                })
                .map(CardDetectorService::getClass)
                .toList();
    }


//    @Test
//    public void detectCardsAndSaveTest() {
//        log.info("Start checking free card detector services");
//
//        var usePaidMethods = true;
//        log.info("use paid methods value is: {}", usePaidMethods);
//
//        var loan = LoanEntity.loan;
//
//        log.info("Using paid methods: {}", usePaidMethods);
//
//        when(absCardDetector.getCardsByPersonCode(any()))
//                .thenReturn(CardDetectorFakeData.getAbsCardDetectorResult());
//
//        when(autoPayCardDetector.getCardsByPersonCode(any()))
//                .thenReturn(CardDetectorFakeData.getAutoPayCardDetectorResult());
//
//        when(davrMobile1CardDetector.getCardsByPersonCode(any()))
//                .thenReturn(CardDetectorFakeData.getDavrMobile1CardDetectorResult());
//
//        when(davrMobile2CardDetector.getCardsByPersonCode(any()))
//                .thenReturn(CardDetectorFakeData.getDavrMobile2CardDetectorResult());
//
//        when(huMoCardDetector.getCardsByPersonCode(any()))
//                .thenReturn(CardDetectorFakeData.getHuMoCardDetectorResult());
//
//        when(masterCardDetector.getCardsByPersonCode(any()))
//                .thenReturn(CardDetectorFakeData.getMasterCardDetectorResult());
//
//        when(visaCardDetector.getCardsByPersonCode(any()))
//                .thenReturn(CardDetectorFakeData.getVisaCardDetectorResult());
//
//        var cardDetectorServiceList = cardBeanRegistrar.cardDetectorServices();
//
//        log.info("Card detector services found: {}", cardDetectorServiceList);
//
//        var expectedResult = Stream.of(
//                PanFake.UZCARD_FAKE_PAN,
//                        PanFake.HUMO_FAKE_PAN, PanFake.AUTOPAY_TOKEN_1, PanFake.AUTOPAY_TOKEN_2,
//                        PanFake.UZCARD_TOKEN_3,
//                        PanFake.HUMO_FAKE_TOKEN_2, PanFake.UZCARD_TOKEN_3, PanFake.HUMO_FAKE_TOKEN_2, PanFake.VISA_FAKE_TOKEN_1,
//                        PanFake.HUMO_FAKE_PAN, PanFake.HUMO_FAKE_PAN_2, PanFake.MASTER_TOKEN_1, PanFake.MASTER_TOKEN_2,
//                        PanFake.VISA_FAKE_TOKEN_1).distinct()
//                .toList();
//        log.info("Expected result is: {}", expectedResult);
//
//        var foundCardsList = cardDetectorServiceList
//                .stream()
//                .filter(z -> {
//                    if (!usePaidMethods) {
//                        return !z.isPaid();
//                    }
//                    return true;
//                })
//                .map(z -> z.getCardsByPersonCode(loan))
//                .filter(SimpleResponse::getStatus)
//                .map(SimpleResponse::getData)
//                .toList();
//
//
//        log.info("Finished card detecting with response: {}", foundCardsList);
//
//        var groupedByProcessing =
//                foundCardsList.stream()
//                        .flatMap(f -> f.getParam()
//                                .stream()
//                                .map(p -> Map.entry(p.getProcessing(), f)))
//                        .collect(
//                                Collectors.groupingBy(
//                                        Map.Entry::getKey,
//                                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
//                                )
//                        );
//
//
//        var actualResult = new ArrayList<>();
//
//        groupedByProcessing.forEach(
//                (processing, groupedResultDTOs) -> groupedResultDTOs.forEach(f ->
//                        f.getParam().stream()
//                                .filter(p -> p.getProcessing() == processing)
//                                .flatMap(p -> p.getParams().stream())
//                                .forEach(z -> actualResult.addAll(z.getCards()))
//                ));
//
//
//        log.info("Actual result is: {}", actualResult);
//        assertEquals(expectedResult, actualResult);
//        log.info("End find card method is over naturally");
//    }

}
