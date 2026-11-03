package uz.nodir.notification.service.business.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.notification.model.enums.TemplateType;
import uz.nodir.notification.service.business.CustomMessageService;
import uz.nodir.notification.service.business.SenderService;
import uz.nodir.notification.utils.PhoneImprover;
import uz.repay.common.model.dto.request.kafka.CourtEventDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CommonUtils;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomMessageServiceImpl implements CustomMessageService {
    private final SenderService senderService;

    @Override
    public ResultData<CoreError> courtMessage(@NonNull CourtEventDTO eventDTO) {
        log.info("Received command to send court message with event DTO: {}", eventDTO);

        var phone = eventDTO.getPhone();

        var phoneImprovedResult = CommonUtils.phoneImprover(phone);

        var phoneCheckResult = PhoneImprover.checkPhoneNumber(phoneImprovedResult);

        if (!phoneCheckResult) {
            var message = "Phone: %s is incorrect! Taken value: %s".formatted(phoneImprovedResult, phone);
            log.error("Method finished with error: {}", message);
            return new ResultData<>(ResultState.BlockOperation, message);
        }

        var sentResult = senderService.send(
                eventDTO.getMessage(),
                phoneImprovedResult,
                TemplateType.CUSTOM,
                eventDTO.getLoanExtId());

        if (!sentResult.getStatus()) {
            log.error("Method send method finished with error... {}", sentResult.getError());
            return new ResultData<>(sentResult.getError(), false);
        }


        var result = ResultData.ok(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );

        log.info("Finished command to send court message with result DTO: {}", result);
        return result;
    }
}
