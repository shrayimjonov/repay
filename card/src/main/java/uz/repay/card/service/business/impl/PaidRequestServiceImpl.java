package uz.repay.card.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.model.dto.paidRequest.response.PaidRequestResponseDTO;
import uz.repay.card.service.business.PaidRequestService;
import uz.repay.card.service.core.PaidRequestMongoDAO;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaidRequestServiceImpl implements PaidRequestService {
    private final PaidRequestMongoDAO dao;

    @Override
    public ResultData<List<PaidRequestResponseDTO>> findRequestByLoanExtId(@NotNull String loanExtId) {
        log.info("Received command to find paid requests by loan ext id: {}", loanExtId);

        var result = dao.findAllByLoanExtId(loanExtId);

        var response = ResultData.ok(
                result
                        .stream()
                        .map(PaidRequestResponseDTO::new)
                        .toList()
        );

        log.info("Finished command to find paid requests by loan ext id: {}", loanExtId);
        return response;
    }

}
