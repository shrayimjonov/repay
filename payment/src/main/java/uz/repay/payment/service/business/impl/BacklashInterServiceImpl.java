package uz.repay.payment.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.request.backlash.BacklashCreateRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.service.business.BacklashInterService;
import uz.repay.payment.service.core.BacklashService;

@Service
@Slf4j
@RequiredArgsConstructor
public class BacklashInterServiceImpl implements BacklashInterService {
    private final BacklashService backlashService;


    @Override
    public ResultData<Boolean> createBacklash(@NonNull BacklashCreateRequestDTO requestDTO) {
        log.info("Received request for create backlash: {}", requestDTO);
        backlashService.create(requestDTO.getLoanId(), requestDTO.getProcessing(),
                requestDTO.getSpentTime(), requestDTO.getType());

        return ResultData.ok(
                true
        );
    }

}
