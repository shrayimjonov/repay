package uz.repay.payment.service.core.impl;

import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.model.enums.Processing;
import uz.repay.payment.redis.entity.Switcher;
import uz.repay.payment.redis.repository.SwitcherRepository;
import uz.repay.payment.service.core.SwitcherRedisService;
import uz.repay.payment.utils.PaymentUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;


@Service
@RequiredArgsConstructor
@Slf4j
public class SwitcherRedisServiceImpl implements SwitcherRedisService {
    @Value("${spring.application.name}")
    private String name;
    private final SwitcherRepository repository;


    @Override
    public Switcher findByName(String switcher) {
        return findOptionalByName(switcher)
                .orElseThrow(() -> new EntityNotFoundException("Switcher with name: %s not found in cache!".formatted(switcher)));
    }

    @Override
    public List<Switcher> findAll() {
        return Lists.newArrayList(repository.findAll());
    }


    //@PreDestroy
    public void turnOfPaymentSwitchers() {
        paymentSwitchers()
                .forEach(p ->
                        findSwitcherAndDoAction(
                                switcher(false),
                                () -> log.info("Payment switcher not found..."),
                                p
                        ));
    }

    private Consumer<Switcher> switcher(Boolean enable) {
        return s -> {
            log.info("Payment switcher found and will be set: {}", enable);
            s.setIsEnabled(enable);
            repository.save(s);
        };
    }

    private void findSwitcherAndDoAction(Consumer<? super Switcher> action, Runnable emptyAction, String name) {
        findAll()
                .stream()
                .filter(t -> t.getName().equals(name))
                .findFirst()
                .ifPresentOrElse(action, emptyAction);
    }

    //@PostConstruct
    @SchedulerLock(name = "Payment-TurnOnSwitchers", lockAtMostFor = "PT4M", lockAtLeastFor = "PT2M")
    public void turnOnPaymentSwitchers() {
        paymentSwitchers()
                .forEach(
                        p -> findSwitcherAndDoAction(
                                switcher(true),
                                () -> createSwitcherAndSave(p),
                                p
                        )
                );
    }

    private void createSwitcherAndSave(String paramName) {
        var switcher = new Switcher(
                UUID.randomUUID(),
                paramName,
                true
        );
        log.info("Created switcher: {}", switcher);
        repository.save(switcher);
    }

    public Optional<Switcher> findOptionalByName(String switcher) {
        return repository
                .findByName(switcher);
    }

    @Override
    public void switchProcessing(Processing processing, Boolean isEnable) {
        var optionalSwitcher = findOptionalByName(processing.name());

        if (optionalSwitcher.isEmpty()) {
            log.info("{} processing switcher not found", processing);
            return;
        }

        var switcher = optionalSwitcher.get();
        log.info("Switcher result: {}", switcher);

        if (isEnable == switcher.getIsEnabled())
            return;

        switcher.setIsEnabled(isEnable);
        repository.save(switcher);
    }

    public List<String> paymentSwitchers() {
        return List.of(
                name,
                PaymentUtils.SWITCHER_REVERSE
        );
    }
}
