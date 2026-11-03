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
import uz.repay.card.model.web.VisaCardFinderResponseFakeData;
import uz.repay.card.service.business.ElmaCardService;
import uz.repay.card.service.business.adapter.impl.detector.VisaCardDetectorImpl;
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
public class VisaCardDetectorServiceImplTest {
    @Mock
    private ElmaCardService elmaCardService;

    @InjectMocks
    private VisaCardDetectorImpl visaCardDetector;



    @Test
    public void success() {
        log.info("Start success result checking from visa card detector service");

        var loan = LoanEntity.loan;


        when(elmaCardService.findClientVisaCardsByCodes(loan.getPinfl()))
                .thenReturn(VisaCardFinderResponseFakeData.success());

       var actualResult = visaCardDetector.getCardsByPersonCode(loan);

       log.info("Actual result is: {}", actualResult);

        var cardSaverDTO = new CardSaverResultDTO<>(
                loan,
                List.of(
                        new CardsParam(
                                Processing.DAVR_VISA,
                                List.of(new CardInfoParam(CardType.token, List.of(PanFake.VISA_FAKE_TOKEN_1)))
                        )
                ),
                true,
                null
        );

        var exceptedResult = new SimpleResponse<>(cardSaverDTO);

        log.info("Excepted result is: {}", exceptedResult);

        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished success result checking from visa detector service");
    }


    @Test
    public void successEmpty() {
        log.info("Start success empty result checking from visa card detector service");

        var loan = LoanEntity.loan;


        when(elmaCardService.findClientVisaCardsByCodes(loan.getPinfl()))
                .thenReturn(VisaCardFinderResponseFakeData.successEmpty());

        var actualResult = visaCardDetector.getCardsByPersonCode(loan);

        log.info("Actual result is: {}", actualResult);


        var exceptedResult = new SimpleResponse<>(
                ResultState.BlockOperation.getCode(),
                "Client doesn't have any cards from Visa!"
        );

        log.info("Excepted result is: {}", exceptedResult);

        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished success empty result checking from visa detector service");
    }

    @Test
    public void failedInternal() {
        log.info("Start failed internal result while checking from visa card detector service");

        var loan = LoanEntity.loan;


        when(elmaCardService.findClientVisaCardsByCodes(loan.getPinfl()))
                .thenReturn(VisaCardFinderResponseFakeData.failed());

        var actualResult = visaCardDetector.getCardsByPersonCode(loan);

        log.info("actual result is: {}", actualResult);


        var exceptedResult = new SimpleResponse<>(
                ResultState.InternalServerError.getCode(),
                "Something went wrong!"
        );

        log.info("Prepared excepted result is: {}", exceptedResult);

        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished failed internal result checking from visa detector service");
    }

}
