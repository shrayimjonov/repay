package uz.repay.autodebit.service.business.v4;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import uz.repay.autodebit.client.webClient.CardWebClient;
import uz.repay.autodebit.client.webClient.LoanWebClient;
import uz.repay.autodebit.client.webClient.PaymentWebClient;
import uz.repay.autodebit.model.loan.response.DebtLoansResult;
import uz.repay.autodebit.model.property.WorkerProperty;
import uz.repay.autodebit.service.core.LoanResultService;
import uz.repay.autodebit.utils.DebtorsUtils;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.utils.GZip.GZipUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
@Slf4j
@RequiredArgsConstructor
public class AutoPaymentServiceV4 {
    private final WorkerProperty workerProperty;
    private final CardWebClient cardWebClient;
    private final PaymentWebClient paymentWebClient;
    private final LoanWebClient loanWebClient;
    private final LoanResultService loanResultService;

    private final AtomicBoolean isActive = new AtomicBoolean(false);


//    @Scheduled(fixedDelayString = "${internal.worker.sleepAfterFinish}")
//    @SchedulerLock(name = "startAutoPaymentV4", lockAtMostFor = "PT3M", lockAtLeastFor = "PT2M")
    public void startAutoPayment() {
        log.info(" ----------------Starting autoPaymentV4! ---------------");
        if (!isActive.get()) {
            log.error("Auto payment is disabled.");
            return;
        }

        log.info("Starting auto payment!");

        loanWebClient.getDebtLoansV2()
                .flatMap(s -> {
                    if (s.getError() != null || s.getResult() == null)
                        return Mono.empty();

                    return Mono.justOrEmpty(GZipUtil.decompress(s.getResult(), new TypeReference<List<DebtLoansResult>>() {
                            }))
                            .filter(SimpleResponse::getStatus) // Только успешные
                            .map(SimpleResponse::getData)
                            .defaultIfEmpty(List.of()); // Если распаковка не удалась, возвращаем пустой список
                })
                .flatMapMany(debtLoansResult -> Mono.justOrEmpty(DebtorsUtils.separateListV3ForEachBranch(debtLoansResult))
                        .defaultIfEmpty(Collections.emptyMap()) // Если `null`, возвращаем пустой Map
                        .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                )
                .parallel(5)
                .runOn(Schedulers.newBoundedElastic(500, 50_000, "auto-payment-worker"))
                .flatMap(entry -> {
                    Branch branch = entry.getKey();
                    List<String> loanList = entry.getValue();

                    if (loanList.isEmpty()) {
                        log.warn("Skipping empty loan list for branch {}", branch);
                        return Mono.empty();
                    }

                    return processBranch(branch, loanList);
                })
                .sequential()
                .subscribe(
                        null, // Нет `onNext`, потому что `Mono<Void>`
                        error -> log.error("Error during auto payment process: {}", error.getMessage()),
                        () -> log.info("Auto payment process completed successfully")
                );
    }

    private Mono<Void> processBranch(Branch branch, List<String> loanList) {
        return Flux.fromIterable(loanList)
                .buffer(workerProperty.getSubListSize()) // Делим на чанки по 5000
                .doOnNext(subList -> log.info("Processing {} loans for branch {}", subList.size(), branch))
                .parallel()
                .runOn(Schedulers.parallel()) // Оптимизируем многопоточное выполнение
                .flatMap(subList -> new PaymentWorkerV4(
                        branch.getCode(),
                        workerProperty,
                        isActive.get(),
                        subList,
                        cardWebClient,
                        loanWebClient,
                        paymentWebClient,
                        UUID.randomUUID(),
                        loanResultService,
                        false
                ).callReactive())
                .sequential()
                .doOnSubscribe(s -> log.info("Started processing loans for branch {}", branch))
                .doOnComplete(() -> log.info("Completed processing loans for branch {}", branch))
                .then();
    }

    public void setActive(boolean state) {
        isActive.set(state);
        if(state)
            startAutoPayment();
        log.info("Auto payment is now {}", state ? "ENABLED" : "DISABLED");
    }

    public boolean isActive() {
        log.info("Get auto payment status. Is: {}", isActive.get());
        return isActive.get();
    }
}
