package uz.nodir.scoring.service.business.main.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.scoring.model.dto.limit.request.CardLimitRequestDTO;
import uz.nodir.scoring.model.dto.limit.response.CardLimitResponseDTO;
import uz.nodir.scoring.service.business.main.CardLimitService;
import uz.nodir.scoring.service.core.bean.LimitServiceRegistrar;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class CardLimitServiceImpl implements CardLimitService {
    private final LimitServiceRegistrar limitServiceRegistrar;

    @Override
    public ResultData<List<CardLimitResponseDTO>> getCardLimits(CardLimitRequestDTO requestDTO) {
        log.info("Received command to get card limits with request dto: {}", requestDTO);

        var serviceResult = limitServiceRegistrar.getCardLimitAdapterByPC(requestDTO.getParams().getProcessing());

        if (!serviceResult.getStatus()) {
            log.error("Finished command. Cause: {}", serviceResult.getError().getMessage());
            return new ResultData<>(serviceResult.getError(), false);
        }

        var service = serviceResult.getData();

        var limitResult = service.getCardLimits(requestDTO.getParams().getCard());

        if (!limitResult.getStatus()) {
            log.error("Finished command. Cause: {}", limitResult.getError().getMessage());
            return new ResultData<>(limitResult.getError(), false);
        }


        var responseDTO = new ResultData<>(
                limitResult.getData()
        );

        log.info("Finished command to get card limits with response dto: {}", responseDTO);
        return responseDTO;
    }
}
