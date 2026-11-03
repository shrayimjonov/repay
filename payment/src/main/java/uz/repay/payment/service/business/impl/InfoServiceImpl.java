package uz.repay.payment.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.payment.NCICourseResponse;
import uz.repay.payment.service.business.InfoService;
import uz.repay.payment.service.web.NCIBillingService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {
    private final NCIBillingService nciBillingService;

    @Override
    public ResultData<List<NCICourseResponse>> getCourse() {
        log.info("Get course for internal...");

        var response = nciBillingService.getCourse();

        if (!response.getStatus())
            return new ResultData<>(
                    response.getError(),
                    false
            );

        return ResultData.ok(
                response.getData().getResult()
        );
    }
}
