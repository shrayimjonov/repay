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
import uz.repay.card.model.entity.Loan;
import uz.repay.card.model.entity.LoanEntity;
import uz.repay.card.model.web.NCICardsByPersonCodeResponseFakeData;
import uz.repay.card.service.business.adapter.impl.detector.ABSCardDetectorImpl;
import uz.repay.card.service.web.impl.NCIBillingServiceImpl;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest
public class ABSCardDetectorServiceImplTest {

    @Mock
    private NCIBillingServiceImpl nciBillingService;

    @InjectMocks
    private ABSCardDetectorImpl absCardDetector;


    @Test
    public void absCardDetectorSuccessTest() {
        log.info("Start success checking abs card detector service");

        Loan loan = LoanEntity.loan;
        log.info("Build fake loan entity: {}", loan);


        var nciBillingSuccessResponse = NCICardsByPersonCodeResponseFakeData.mockNCIBillingCarsByPersonCodeSuccessResponse();
        log.info("Mock nciBillingSuccessResponse: {}", nciBillingSuccessResponse);

        when(nciBillingService.getCardsByPersonCode(loan.getPinfl()))
                .thenReturn(nciBillingSuccessResponse);

        var absDetectorResult = absCardDetector.getCardsByPersonCode(loan);


        var cardSaverDTO = new CardSaverResultDTO<>(
                loan,
                List.of(
                        new CardsParam(
                                Processing.UZCARD,
                                List.of(new CardInfoParam(CardType.pan, List.of(PanFake.UZCARD_FAKE_PAN)))
                        )
                ),
                false,
                null
        );

        var result = new SimpleResponse<>(cardSaverDTO);
        log.info("Prepared fake result: {}", result);


        assertEquals(absDetectorResult, result);
        log.info("Finished success checking abs card detector service");
    }


    @Test
    public void absCardDetectorFailedTest() {
        log.info("Start failed checking abs card detector service");

        Loan loan = LoanEntity.loan;
        log.info("Build fake loan entity: {}", loan);


        var nciBillingSuccessResponse = NCICardsByPersonCodeResponseFakeData.mockNCIBillingCarsByPersonCodeFailedResponse();
        log.info("Mock nciBillingSuccessResponse: {}", nciBillingSuccessResponse);

        when(nciBillingService.getCardsByPersonCode(loan.getPinfl()))
                .thenReturn(nciBillingSuccessResponse);

        var absDetectorResult = absCardDetector.getCardsByPersonCode(loan);

        var message = "Is unSuccess situation for mock";

        var result = new SimpleResponse<>(ResultState.InternalServerError.getCode(), message);
        log.info("Prepared fake result: {}", result);


        assertEquals(absDetectorResult, result);
        log.info("Finished failed checking abs card detector service");
    }

    @Test
    public void absCardDetectorEmptyResultTest() {
        log.info("Start empty result checking abs card detector service");

        Loan loan = LoanEntity.loan;
        log.info("Build fake loan entity: {}", loan);


        var nciBillingSuccessResponse = NCICardsByPersonCodeResponseFakeData.nciBillingCarsByPersonCodeEmptySuccessResponse();
        log.info("Mock nciBillingSuccessResponse: {}", nciBillingSuccessResponse);

        when(nciBillingService.getCardsByPersonCode(loan.getPinfl()))
                .thenReturn(nciBillingSuccessResponse);

        var absDetectorResult = absCardDetector.getCardsByPersonCode(loan);

         String message = "Client doesn't have any cards in ABS!";

        var result = new SimpleResponse<>(ResultState.BlockOperation.getCode(), message);
        log.info("Prepared fake result: {}", result);


        assertEquals(absDetectorResult, result);
        log.info("Finished empty result checking abs card detector service");
    }

}
