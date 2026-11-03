package uz.nodir.notification.service.core.component;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.stereotype.Component;
import uz.nodir.notification.service.core.FraudRedisDAO;


@Component
@Slf4j
@RequiredArgsConstructor
public class FraudCleanerComponent {
    private final FraudRedisDAO fraudRedisDAO;

    @PreDestroy
    @SchedulerLock(name = "notificationDeleteFrauds", lockAtMostFor = "PT2M", lockAtLeastFor = "PT1M")
    public void deleteFraudMessages() {
        log.warn("Delete all frauds from CPU before destroying");
        fraudRedisDAO.deleteAll();
    }
}
