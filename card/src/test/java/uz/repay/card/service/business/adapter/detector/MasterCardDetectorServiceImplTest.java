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
import uz.repay.card.model.dto.master.request.MasterCustomerCardsByPINFLRequest;
import uz.repay.card.model.entity.LoanEntity;
import uz.repay.card.model.web.MasterCardFinderResponseFakeData;
import uz.repay.card.service.business.adapter.impl.detector.MasterCardDetectorImpl;
import uz.repay.card.service.web.MasterClientService;
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
public class MasterCardDetectorServiceImplTest {
    @Mock
    private MasterClientService masterClientService;

    @InjectMocks
    private MasterCardDetectorImpl masterCardDetector;


    @Test
    public void success() {
        log.info("Start success result checking from master card detector service");

        var loan = LoanEntity.loan;

        var request = new MasterCustomerCardsByPINFLRequest(loan.getPinfl());

        when(masterClientService.customerCardsByPersonCode(request))
                .thenReturn(MasterCardFinderResponseFakeData.success());

       var actualResult = masterCardDetector.getCardsByPersonCode(loan);

       log.info("actual result is: {}", actualResult);

        var cardSaverDTO = new CardSaverResultDTO<>(
                loan,
                List.of(
                        new CardsParam(
                                Processing.DAVR_MASTER_CARD,
                                List.of(new CardInfoParam(CardType.token, List.of(PanFake.MASTER_TOKEN_1)))
                        )
                ),
                true,
                null
        );

        var exceptedResult = new SimpleResponse<>(cardSaverDTO);

        log.info("Prepared excepted result is: {}", exceptedResult);

        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished success result checking from master detector service");
    }


    @Test
    public void successEmpty() {
        log.info("Start success empty result checking from master card detector service");

        var loan = LoanEntity.loan;

        var request = new MasterCustomerCardsByPINFLRequest(loan.getPinfl());

        when(masterClientService.customerCardsByPersonCode(request))
                .thenReturn(MasterCardFinderResponseFakeData.successEmpty());

        var actualResult = masterCardDetector.getCardsByPersonCode(loan);

        log.info("actual result is: {}", actualResult);


        var exceptedResult = new SimpleResponse<>(
                ResultState.BlockOperation.getCode(),
                "Client doesn't have any cards from MASTER BPC!"
        );

        log.info("Prepared excepted result is: {}", exceptedResult);

        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished success empty result checking from master detector service");
    }

    @Test
    public void failedInternal() {
        log.info("Start failed internal result while checking from master card detector service");

        var loan = LoanEntity.loan;

        var request = new MasterCustomerCardsByPINFLRequest(loan.getPinfl());

        when(masterClientService.customerCardsByPersonCode(request))
                .thenReturn(MasterCardFinderResponseFakeData.failedInternal());

        var actualResult = masterCardDetector.getCardsByPersonCode(loan);

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

        log.info("Finished failed internal result checking from master detector service");
    }

    @Test
    public void failedFromPC() {
        log.info("Start failed from PC result while checking from master card detector service");

        var loan = LoanEntity.loan;

        var request = new MasterCustomerCardsByPINFLRequest(loan.getPinfl());

        when(masterClientService.customerCardsByPersonCode(request))
                .thenReturn(MasterCardFinderResponseFakeData.failedFromPC());

        var actualResult = masterCardDetector.getCardsByPersonCode(loan);

        log.info("actual result is: {}", actualResult);


        var exceptedResult = new SimpleResponse<>(
                ResultState.ThirdPartyError.getCode(),
                "Client not found!"
        );

        log.info("Prepared excepted result is: {}", exceptedResult);

        assertEquals(
                exceptedResult,
                actualResult
        );

        log.info("Finished failed from PC result checking from master detector service");
    }
}
