package uz.repay.report.service.business.analysis.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.report.model.entity.MostDebitUsers;
import uz.repay.report.service.business.analysis.AnalysisService;
import uz.repay.report.service.dao.TransactionDAO;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {
    private final TransactionDAO transactionDAO;

    @Override
    public ResultData<List<MostDebitUsers>> mostDebitUsers(int limit) {
        log.info("Received command to get most debit users by limit: {}", limit);

        var result = transactionDAO.getMostDebitUsers(limit);

        var responseDTO = ResultData.ok(result);

        log.info("Finished command to get most debit users with size: {}", responseDTO.getResult().size());
        return responseDTO;
    }
}
