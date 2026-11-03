package uz.repay.card.service.core.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.repay.card.service.core.CardDAO;
import uz.repay.card.util.ServiceUtils;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckExpireCardsComponent {
    private final CardDAO cardDAO;


    @Scheduled(cron = "0 0 2 1,16 * ?") // every month in 1st day and 16th day
    @SchedulerLock(name = "changeExpiredCardsToNotActive", lockAtMostFor = "PT1H", lockAtLeastFor = "PT30M")
    public void changeExpiredCardsToNotActive() {
        var cardsList = cardDAO.getAll(true);

        log.info("Expired cards to date {} size of: {}",
                LocalDate.now().getMonth() + " " + LocalDate.now().getYear(),
                cardsList.size());

        if (cardsList.isEmpty())
            return;


        cardsList
                .forEach(
                        c -> {
                            if (ServiceUtils.isExpiredCard(c.getExpiry())) {
                                c.setIsActive(false);
                                cardDAO.update(c);
                            }
                        }
                );
    }

}
