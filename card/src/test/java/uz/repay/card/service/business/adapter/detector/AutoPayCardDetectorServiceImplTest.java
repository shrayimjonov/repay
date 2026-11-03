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
import uz.repay.card.model.dto.svgate.request.params.SVGateAutoPayCardsParam;
import uz.repay.card.model.entity.EPosEntity;
import uz.repay.card.model.entity.LoanEntity;
import uz.repay.card.model.web.AutoPayResponseFakeData;
import uz.repay.card.model.web.LoanInfoResponseFakeData;
import uz.repay.card.service.business.adapter.impl.detector.AutoPayCardDetectorImpl;
import uz.repay.card.service.core.impl.EPosDAOImpl;
import uz.repay.card.service.web.impl.NCIBillingServiceImpl;
import uz.repay.card.service.web.impl.SVGateClientServiceImpl;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.CardType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ResultState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;



@SpringBootTest
@Slf4j
@ExtendWith(SpringExtension.class)
public class AutoPayCardDetectorServiceImplTest {
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private NCIBillingServiceImpl nciBillingService;

    @Mock
    private EPosDAOImpl ePosDAO;

    @Mock
    private SVGateClientServiceImpl uzCardService;

    @InjectMocks
    private AutoPayCardDetectorImpl autoPayCardDetector;

    private final static String FAKE_LOAN_EXT_ID = "01121158076";

    @Test
    public void autoPayCardDetectorSuccessTest() {
        log.info("Start success result checking autoPay card detector service");

        when(nciBillingService.getLoanInfo(FAKE_LOAN_EXT_ID))
                .thenReturn(LoanInfoResponseFakeData.buildSuccessResponse());

        var loanInfoResult = nciBillingService.getLoanInfo(FAKE_LOAN_EXT_ID);

        when(ePosDAO.getAll())
                .thenReturn(EPosEntity.findAll());

        var loan = LoanEntity.loan;

        var request = new SVGateAutoPayCardsParam(
                loan,
                loanInfoResult.getResult(),
                EPosEntity.AUTOPAY_EPOS
        );

        when(uzCardService.getAutoPayCards(request))
                .thenReturn(AutoPayResponseFakeData.buildSuccessResponse());

        var autoPayResult = autoPayCardDetector.getCardsByPersonCode(loan);

        var cardSaverDTO = new CardSaverResultDTO<>(
                loan,
                List.of(
                        new CardsParam(
                                Processing.SVGATE_AUTOPAY,
                                List.of(
                                        new CardInfoParam(
                                                CardType.token,
                                                List.of(
                                                        PanFake.AUTOPAY_TOKEN_1,
                                                        PanFake.AUTOPAY_TOKEN_2
                                                )
                                        )
                                )
                        )
                ),
                true,
                AutoPayResponseFakeData.buildAutoPaySuccessList().getResult()
        );

        var checkResult = new SimpleResponse<>(cardSaverDTO);
        log.info("Prepared fake result: {}", checkResult);

        assertEquals(
                autoPayResult,
                checkResult
        );
        log.info("Finished success checking abs card detector service");
    }


    @Test
    public void autoPayCardDetectorLoanInfoFailedTest() {
        log.info("Start loan failed result checking autoPay card detector service");

        when(nciBillingService.getLoanInfo(FAKE_LOAN_EXT_ID))
                .thenReturn(LoanInfoResponseFakeData.buildErrorResponse());

        var loan = LoanEntity.loan;

        var autoPayResult = autoPayCardDetector.getCardsByPersonCode(loan);


        var checkResult = new SimpleResponse<>(
                ResultState.InternalServerError.getCode(),
                "158076 loan in 01121 is closed"
        );

        log.info("Prepared fake result: {}", checkResult);

        assertEquals(
                checkResult,
                autoPayResult
        );
        log.info("Finished loan f checking abs card detector service");
    }

    @Test
    public void autoPayCardDetectorEposEmptyTest() {
        log.info("Start ePos empty result checking autoPay card detector service");

        when(nciBillingService.getLoanInfo(FAKE_LOAN_EXT_ID))
                .thenReturn(LoanInfoResponseFakeData.buildSuccessResponse());

        when(ePosDAO.getAll())
                .thenReturn(List.of());

        var loan = LoanEntity.loan;


        var autoPayResult = autoPayCardDetector.getCardsByPersonCode(loan);


        var checkResult = new SimpleResponse<>(
                ResultState.InternalServerError.getCode(),
                "EPos not found for: %s %s".formatted(Processing.SVGATE_AUTOPAY, Branch._01121)
        );
        log.info("Prepared fake result: {}", checkResult);

        assertEquals(
                autoPayResult,
                checkResult
        );
        log.info("Finished ePos empty checking abs card detector service");
    }


    @Test
    public void autoPayCardDetectorAutoPayFeignErrorTest() {
        log.info("Start feign error result checking autoPay card detector service");

        when(nciBillingService.getLoanInfo(FAKE_LOAN_EXT_ID))
                .thenReturn(LoanInfoResponseFakeData.buildSuccessResponse());

        var loanInfoResult = nciBillingService.getLoanInfo(FAKE_LOAN_EXT_ID);

        when(ePosDAO.getAll())
                .thenReturn(EPosEntity.findAll());

        var loan = LoanEntity.loan;

        var request = new SVGateAutoPayCardsParam(
                loan,
                loanInfoResult.getResult(),
                EPosEntity.AUTOPAY_EPOS
        );

        when(uzCardService.getAutoPayCards(request))
                .thenReturn(AutoPayResponseFakeData.buildFeignFailedResponse());

        var autoPayResult = autoPayCardDetector.getCardsByPersonCode(loan);


        var checkResult = new SimpleResponse<>(
                ResultState.InternalServerError.getCode(),
                "Something went wrong!"
        );
        log.info("Prepared fake result: {}", checkResult);

        assertEquals(
                autoPayResult,
                checkResult
        );
        log.info("Finished feign error checking abs card detector service");
    }

    @Test
    public void autoPayCardDetectorAutoPayResultErrorTest() {
        log.info("Start autoPay error result checking autoPay card detector service");

        when(nciBillingService.getLoanInfo(FAKE_LOAN_EXT_ID))
                .thenReturn(LoanInfoResponseFakeData.buildSuccessResponse());

        var loanInfoResult = nciBillingService.getLoanInfo(FAKE_LOAN_EXT_ID);

        when(ePosDAO.getAll())
                .thenReturn(EPosEntity.findAll());

        var loan = LoanEntity.loan;

        var request = new SVGateAutoPayCardsParam(
                loan,
                loanInfoResult.getResult(),
                EPosEntity.AUTOPAY_EPOS
        );

        when(uzCardService.getAutoPayCards(request))
                .thenReturn(AutoPayResponseFakeData.buildFailedResponse());

        var autoPayResult = autoPayCardDetector.getCardsByPersonCode(loan);


        var checkResult = new SimpleResponse<>(
                ResultState.ThirdPartyError.getCode(),
                "Cards not found!"
        );
        log.info("Prepared fake result: {}", checkResult);

        assertEquals(
                autoPayResult,
                checkResult
        );
        log.info("Finished autoPay error checking abs card detector service");
    }


    @Test
    public void autoPayCardDetectorAutoPayResultEmptyTest() {
        log.info("Start autoPay empty result checking autoPay card detector service");

        when(nciBillingService.getLoanInfo(FAKE_LOAN_EXT_ID))
                .thenReturn(LoanInfoResponseFakeData.buildSuccessResponse());

        var loanInfoResult = nciBillingService.getLoanInfo(FAKE_LOAN_EXT_ID);

        when(ePosDAO.getAll())
                .thenReturn(EPosEntity.findAll());

        var loan = LoanEntity.loan;

        var request = new SVGateAutoPayCardsParam(
                loan,
                loanInfoResult.getResult(),
                EPosEntity.AUTOPAY_EPOS
        );

        when(uzCardService.getAutoPayCards(request))
                .thenReturn(AutoPayResponseFakeData.buildSuccessEmptyResponse());

        var autoPayResult = autoPayCardDetector.getCardsByPersonCode(loan);


        var checkResult = new SimpleResponse<>(
                ResultState.BlockOperation.getCode(),
               "Client doesn't have any cards in AutoPay!"
        );

        log.info("Prepared fake result: {}", checkResult);

        assertEquals(
                autoPayResult,
                checkResult
        );
        log.info("Finished autoPay empty checking abs card detector service");
    }

}
