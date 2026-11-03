package uz.repay.autodebit.service.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.model.result.request.LoanResultRequestDTO;
import uz.repay.autodebit.model.result.response.LoanResultResponseDTO;
import uz.repay.autodebit.mongo.entity.LoanResult;
import uz.repay.autodebit.mongo.repository.LoanResultRepository;
import uz.repay.autodebit.service.core.LoanResultService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.utils.BranchUtils;
import uz.repay.common.utils.PageUtils;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanResultServiceImpl implements LoanResultService {
    private final LoanResultRepository repository;

    @Override
    public ResultData<LoanResultResponseDTO> getResultsByLoan(LoanResultRequestDTO requestDTO) {
        log.error("Get results by loan: {}", requestDTO.getParams().getLoanExtId());
        requestDTO.setSortBy("createdTime");
        var pageable =
                PageUtils.pageableBuilder(requestDTO);
        var page = repository.findByLoanExtId(requestDTO.getParams().getLoanExtId(), pageable);

        return ResultData.ok(
                new LoanResultResponseDTO(
                        page
                )
        );
    }

    @Override
    public void save(String loan, String message, String sessionId, UUID workerId) {
        log.info("Save loanResult: {} {} {} {}", loan, message, sessionId, workerId);
        var preparedResult = new LoanResult(
                UUID.randomUUID(),
                BranchUtils.getBranchByLoan(loan),
                loan,
                message,
                sessionId,
                workerId
        );
        repository.save(preparedResult);
    }
}
