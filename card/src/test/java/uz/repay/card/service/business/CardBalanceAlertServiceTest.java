package uz.repay.card.service.business;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.core.request.autopay.request.AutoPayPushRequestDTO;
import uz.repay.card.model.entity.LoanCard;
import uz.repay.card.model.entity.LoanCardEntity;
import uz.repay.card.model.web.ForcePayResponseFakeData;
import uz.repay.card.model.web.UzCardResponseFakeData;
import uz.repay.card.redis.entity.Switcher;
import uz.repay.card.service.business.impl.CardBalanceAlertServiceImpl;
import uz.repay.card.service.core.LoanCardDAO;
import uz.repay.card.service.core.SwitcherRedisService;
import uz.repay.card.service.web.PaymentClientService;
import uz.repay.card.service.web.UzCardService;
import uz.repay.common.model.dto.request.kafka.CardCreditEventDTO;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.utils.CardUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@Slf4j
@ExtendWith(SpringExtension.class)
public class CardBalanceAlertServiceTest {

    @Mock
    private LoanCardDAO loanCardDAO;

    @Mock
    private PaymentClientService paymentClientService;

    @Mock
    private SwitcherRedisService switcherRedisService;

    @Mock
    private UzCardService uzCardService;

    @Mock
    private RedissonClient redissonClient;


    @InjectMocks
    private CardBalanceAlertServiceImpl cardBalanceAlertService;

    @BeforeEach
    void setUp() {
        RLock lock = Mockito.mock(RLock.class);
        when(redissonClient.getLock(any())).thenReturn(lock); // Мокируем возврат Lock
    }

    @Test
    public void alertUzCardDavrMobile() {
        log.info("Start testing alert from DavrMobile");

        long amount = 40000L;

        String switcherName = "payment";

        when(switcherRedisService.findByName(switcherName))
                .thenReturn(
                        new Switcher(
                                UUID.randomUUID(),
                                switcherName,
                                true
                                ,true
                        )
                );

        when(uzCardService.getP2PInfo(any()))
                .thenReturn(UzCardResponseFakeData.fakeP2PInfoSuccessResponse());

        List<LoanCard> fakeLoanCards = LoanCardEntity.fakeUzCardLoanCards();

        when(loanCardDAO.findByCardPan(CardUtils.encryptPan(PanFake.UZCARD_FAKE_PAN)))
                .thenReturn(fakeLoanCards);

        log.info("Fake loanCard returns: {}", fakeLoanCards);

        var fakePayResponse = ForcePayResponseFakeData.success();

        log.info("use any() request to force pay");

        when(paymentClientService.forcePay(any()))
                .thenReturn(fakePayResponse);

        log.info("fake payment returns: {}", fakePayResponse);

        var creditEventDTO = new CardCreditEventDTO(
                PanFake.UZCARD_FAKE_PAN,
                amount,
                "022569898989"
        );


        cardBalanceAlertService.alertedFromDavrMobile(creditEventDTO);

        log.info("Finished testing alert from DavrMobile");
    }


    @Test
    public void alertAutoPay() {
        log.info("Start testing alert from autoPay");

        long amount = 40000L;

        String switcherName = Processing.SVGATE_AUTOPAY.name();

        when(switcherRedisService.findByName(switcherName))
                .thenReturn(
                        new Switcher(
                                UUID.randomUUID(),
                                switcherName,
                                true,
                                true
                        )
                );


        List<LoanCard> fakeLoanCards = LoanCardEntity.fakeUzCardLoanCards();

        when(loanCardDAO.findByCardPan(CardUtils.encryptPan(PanFake.UZCARD_TOKEN_1)))
                .thenReturn(fakeLoanCards);

        log.info("Fake loanCard returns: {}", fakeLoanCards);

        var fakePayResponse = ForcePayResponseFakeData.success();

        log.info("use any() request to force pay");

        when(paymentClientService.forcePay(any()))
                .thenReturn(fakePayResponse);

        log.info("fake payment returns: {}", fakePayResponse);

        var creditEventDTO = new AutoPayPushRequestDTO(
                PanFake.UZCARD_FAKE_PAN,
                PanFake.UZCARD_TOKEN_1,
                null,
                null,
                null,
                amount,
                "860",
                amount,
                null
        );


        cardBalanceAlertService.pushToLoansWithAlertedCard(creditEventDTO, LocalDateTime.now());

        log.info("Finished testing alert from autoPay");
    }

}