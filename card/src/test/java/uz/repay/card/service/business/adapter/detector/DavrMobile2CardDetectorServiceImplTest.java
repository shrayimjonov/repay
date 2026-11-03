package uz.repay.card.service.business.adapter.detector;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.core.request.card.CardInfoParam;
import uz.repay.card.model.dto.core.request.card.CardsParam;
import uz.repay.card.model.dto.core.response.card.CardSaverResultDTO;
import uz.repay.card.model.entity.LoanEntity;
import uz.repay.card.model.web.DavrMobile2ResponseFakeData;
import uz.repay.card.service.business.adapter.impl.detector.DavrMobile2CardDetectorImpl;
import uz.repay.card.service.web.DM2Service;
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
public class DavrMobile2CardDetectorServiceImplTest {
    @Mock
    private DM2Service dmService;

    @InjectMocks
    private DavrMobile2CardDetectorImpl davrMobile2CardDetector;

    @Test
    public void testSuccessResult() {
        log.info("Start success result checking davr mobile 2.0 card detector service");

        var loan = LoanEntity.loan;

        when(dmService.getClientCardsByPINFL(loan.getPinfl()))
                .thenReturn(DavrMobile2ResponseFakeData.successResponse());

        var result = davrMobile2CardDetector.getCardsByPersonCode(loan);
        log.info("Detector result is: {}", result);

        var cardSaverDTO = new CardSaverResultDTO<>(
                loan,
                List.of(
                        new CardsParam(
                                Processing.UZCARD,
                                List.of(new CardInfoParam(CardType.token, List.of(PanFake.UZCARD_TOKEN_1)))
                        )
                ),
                false,
                null
        );

        var checkResult = new SimpleResponse<>(cardSaverDTO);
        log.info("Prepared fake result: {}", checkResult);

        assertEquals(
                checkResult,
                result
        );

        log.info("Finished success checking davr mobile 2.0 card detector service");
    }

    @Test
    public void testSuccessEmptyResult() {
        log.info("Start success empty result checking davr mobile 2.0 card detector service");

        var loan = LoanEntity.loan;

        when(dmService.getClientCardsByPINFL(loan.getPinfl()))
                .thenReturn(DavrMobile2ResponseFakeData.successEmptyResponse());

        var result = davrMobile2CardDetector.getCardsByPersonCode(loan);
        log.info("Detector result is: {}", result);

        var exceptedResult = new SimpleResponse<>(
                ResultState.BlockOperation.getCode(),
                "Client doesn't have any cards in Davr Mobile 2.0!"
        );
        log.info("Prepared fake result: {}", exceptedResult);

        assertEquals(
                exceptedResult,
                result
        );

        log.info("Finished success empty checking davr mobile 2.0 card detector service");
    }

    @Test
    public void testFailedResult() {
        log.info("Start failed result checking davr mobile 2.0 card detector service");

        var loan = LoanEntity.loan;

        when(dmService.getClientCardsByPINFL(loan.getPinfl()))
                .thenReturn(DavrMobile2ResponseFakeData.failedResponse());

        var actualResult = davrMobile2CardDetector.getCardsByPersonCode(loan);
        log.info("Detector result is: {}", actualResult);



        var exceptedResult = new SimpleResponse<>(
                ResultState.InternalServerError.getCode(),
                "Something went wrong!"

        );
        log.info("Prepared fake result: {}", exceptedResult);

        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished failed checking davr mobile 2.0 card detector service");
    }
}
