package uz.repay.card.service.business.adapter.detector;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.core.request.card.CardInfoParam;
import uz.repay.card.model.dto.core.request.card.CardsParam;
import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.entity.LoanEntity;
import uz.repay.card.model.web.HuMoCardFinderFakeData;
import uz.repay.card.service.business.adapter.impl.detector.HuMoCardDetectorImpl;
import uz.repay.card.service.web.HuMoClientService;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;



@SpringBootTest
@Slf4j
@ExtendWith(SpringExtension.class)
public class HuMoCardDetectorServiceImplTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private HuMoClientService huMoClientService;

    @InjectMocks
    private HuMoCardDetectorImpl huMoCardDetector;


    @Test
    public void testSuccess() {
        log.info("Start success result checking huMo card detector service");

        var loan = LoanEntity.loan;

        when(huMoClientService.getClientCardsByPINFLV2(loan.getPinfl()))
                .thenReturn(HuMoCardFinderFakeData.success());

        var actualResult = huMoCardDetector.getCardsByPersonCode(loan);

        log.info("huMo result is: {}", actualResult);

        var cardSaverDTO = new CardSaverResultDTO<>(
                loan,
                List.of(
                        new CardsParam(
                                Processing.HUMO,
                                List.of(new CardInfoParam(CardType.pan, List.of(PanFake.HUMO_FAKE_PAN)))
                        )
                ),
                true,
                null
        );

        var exceptedResult = new SimpleResponse<>(cardSaverDTO);

        log.info("Prepared excepted dto: {}", exceptedResult);


        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished success result checking huMo detector service");
    }

    @Test
    public void testSuccessEmpty() {
        log.info("Start success empty result checking huMo card detector service");

        var loan = LoanEntity.loan;

        when(huMoClientService.getClientCardsByPINFLV2(loan.getPinfl()))
                .thenReturn(HuMoCardFinderFakeData.successEmpty());

        var actualResult = huMoCardDetector.getCardsByPersonCode(loan);

        log.info("huMo result is: {}", actualResult);


        var exceptedResult = new SimpleResponse<>(
                ResultState.BlockOperation.getCode(),
                "Client doesn't have any cards in HuMo!"
        );

        log.info("Prepared excepted dto: {}", exceptedResult);


        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished success empty result checking huMo detector service");
    }

    @Test
    public void testFailedFromHuMo() {
        log.info("Start failed result from pc checking huMo card detector service");

        var loan = LoanEntity.loan;

        when(huMoClientService.getClientCardsByPINFLV2(loan.getPinfl()))
                .thenReturn(HuMoCardFinderFakeData.failedFromPC());

        var actualResult = huMoCardDetector.getCardsByPersonCode(loan);

        log.info("huMo result is: {}", actualResult);


        var exceptedResult = new SimpleResponse<>(
                ResultState.ThirdPartyError.getCode(),
                "Cards not found!"
        );

        log.info("Prepared excepted dto: {}", exceptedResult);


        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished failed result from pc checking huMo detector service");
    }


    @Test
    public void testFailedInternal() {
        log.info("Start failed internal result while checking huMo card detector service");

        var loan = LoanEntity.loan;

        when(huMoClientService.getClientCardsByPINFLV2(loan.getPinfl()))
                .thenReturn(HuMoCardFinderFakeData.failedInternal());

        var actualResult = huMoCardDetector.getCardsByPersonCode(loan);

        log.info("huMo result is: {}", actualResult);


        var exceptedResult = new SimpleResponse<>(
                ResultState.InternalServerError.getCode(),
                "Something went wrong!"
        );

        log.info("Prepared excepted dto: {}", exceptedResult);


        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished failed internal result while checking huMo detector service");
    }

}
