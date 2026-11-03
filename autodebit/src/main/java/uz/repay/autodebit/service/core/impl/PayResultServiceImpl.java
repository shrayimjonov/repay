package uz.repay.autodebit.service.core.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.model.core.response.AutoPayResult;
import uz.repay.autodebit.model.core.response.AutoPayResultV2;
import uz.repay.autodebit.model.core.response.PayResultResponseDTO;
import uz.repay.autodebit.mongo.entity.PayResult;
import uz.repay.autodebit.mongo.repository.PayResultRepository;
import uz.repay.autodebit.service.core.PayResultService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayResultServiceImpl implements PayResultService {
    private final PayResultRepository repository;


    @Override
    public void create(AutoPayResult s) {
        log.info("Creating result in mongo branch: {} and session: {}", s.getBranch(), s.getSessionID());
        var entity = entityBuilder(s);

        repository.save(entity);
    }

    @Override
    public void createV2(AutoPayResultV2 s) {
        log.info("Creating result in mongo branch: {} and session: {}", s.getBranch(), s.getSessionID());
        var entity = entityBuilderV2(s);

        repository.save(entity);
    }

    private static PayResult entityBuilder(AutoPayResult s) {
        return new PayResult(
                UUID.randomUUID(),
                s.getBranch(),
                s.getSuccessList(),
                s.getFailedList(),
                s.getSessionID()
        );
    }

    private static PayResult entityBuilderV2(AutoPayResultV2 s) {
        return new PayResult(
                UUID.randomUUID(),
                s.getBranch(),
                s.getSuccessList(),
                s.getFailedList(),
                s.getSessionID()
        );
    }

    @Override
    public ResultData<PayResultResponseDTO> getResultsBySessionID(@NonNull String sessionId) {

        if (!sessionId.matches("^(?=.{32}$)[A-Z0-9]*$"))
            return new ResultData<>(ResultState.WrongParams, "Session Id not matched rules");

        var resultOptional = repository.findBySessionId(sessionId);

        return resultOptional.map(
                        payResult -> new ResultData<>(
                                new PayResultResponseDTO(payResult)
                        )
                )
                .orElseGet(
                        () -> new ResultData<>(ResultState.EntityNotFound,
                                "Result with sessionId: %s not found".formatted(sessionId)
                        )
                );

    }

    @Override
    public ResultData<List<PayResultResponseDTO>> getResultsByLoanExt(@NonNull String loanExt) {
        return ResultData.ok(
                repository.findAllByFailedListContains(loanExt)
                        .stream()
                        .map(PayResultResponseDTO::new)
                        .toList()
        );

    }
}
