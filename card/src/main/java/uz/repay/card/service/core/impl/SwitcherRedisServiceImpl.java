package uz.repay.card.service.core.impl;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.repay.card.redis.entity.Switcher;
import uz.repay.card.redis.repository.SwitcherRepository;
import uz.repay.card.service.core.SwitcherRedisService;
import uz.repay.common.exception.EntityNotFoundException;
import uz.repay.common.model.enums.Processing;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
@RequiredArgsConstructor
@Slf4j
public class SwitcherRedisServiceImpl implements SwitcherRedisService {
    private final SwitcherRepository repository;

    @Override
    public Switcher findByName(String switcher) {
        return repository
                .findByName(switcher)
                .orElseThrow(() -> new EntityNotFoundException("Switcher with name: %s not found in cache!".formatted(switcher)));
    }

    @Override
    public List<Switcher> findAll() {
        return  StreamSupport.stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());

    }

    @Override
    public Switcher updateStatus(UUID id, Boolean newValue) {
        log.info("Turn switcher for: {}", id);
        var switcher = findById(id);

        log.info("Switcher: {} status is: {} and will be changed to: {}", switcher.getName(), switcher.getIsEnabled(), newValue);
        if (Objects.equals(newValue, switcher.getIsEnabled()) && Boolean.FALSE.equals(switcher.getUpdatedBySystem())) {
            return switcher;
        }

        switcher.setIsEnabled(newValue);
        switcher.setUpdatedBySystem(false);
        return repository.save(switcher);
    }

    private Switcher findById(UUID id) {
        log.info("Find switcher by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Switcher with id: %s not found in cache!".formatted(id)));
    }

//    @PreDestroy
    @SchedulerLock(name = "turnOfProcessingSwitchers", lockAtMostFor = "PT5M", lockAtLeastFor = "PT3M")
    public void turnOfProcessingSwitchers() {
        var switchersList = findAll();

        Arrays.stream(Processing
                        .values())
                .forEach(p -> turnOffProcessingSwitchers(switchersList, p));
    }

    private void turnOffProcessingSwitchers(List<Switcher> switchersList, Processing processing) {
        switchersList
                .stream()
                .filter(s -> s.getName().equals(processing.name()))
                .findFirst()
                .ifPresentOrElse(s -> {
                            if (Boolean.FALSE.equals(s.getIsEnabled())) {
                                log.info("{} switcher is already turned off", processing);
                                return;
                            }
                            log.info("{} switcher found and will be turn off", processing);
                            s.setIsEnabled(false);
                            s.setUpdatedBySystem(true);
                            repository.save(s);

                        },
                        () -> log.info("{} switcher not found...", processing));
    }

    @PostConstruct
    @SchedulerLock(name = "createProcessingSwitchers", lockAtMostFor = "PT5M", lockAtLeastFor = "PT3M")
    public void createProcessingSwitchers() {
        var switchersList = findAll();

        Arrays.stream(Processing
                        .values())
                .forEach(p -> checkProcessingSwitchers(switchersList, p));


    }

    @Scheduled(cron = "0 0 0/6 * * *")
    public void scheduleProcessingSwitchers() {
        var switchersList = findAll();

        Arrays.stream(Processing
                        .values())
                .forEach(p -> checkProcessingSwitchers(switchersList, p));


    }

    private void checkProcessingSwitchers(List<Switcher> switchersList, Processing processing) {
        switchersList
                .stream()
                .filter(s -> s.getName().equals(processing.name()))
                .findFirst()
                .ifPresentOrElse(s -> {
                            log.info("{} switcher already has in Cache", processing);
                            if (Boolean.FALSE.equals(s.getIsEnabled()) && isUpdatedBySystem(s)) {
                                log.info("Switcher was turned off. Now started again");
                                s.setIsEnabled(true);
                                s.setUpdatedBySystem(true);
                                repository.save(s);
                            } else if (s.getUpdatedBySystem() == null) {
                                s.setUpdatedBySystem(false);
                                repository.save(s);
                            }
                        },

                        () -> {
                            log.info("{} switcher not found and will be created...", processing);
                            var switcher = createSwitcher(processing);
                            repository.save(switcher);
                        });
    }

    private static Switcher createSwitcher(Processing processing) {
        var switcher = new Switcher(
                UUID.randomUUID(),
                processing.name(),
                true,
                true
        );
        log.info("Created switcher: {}", switcher);
        return switcher;
    }

    private static boolean isUpdatedBySystem(Switcher switcher) {
        return switcher.getUpdatedBySystem() == null || Boolean.TRUE.equals(switcher.getUpdatedBySystem());
    }

}
