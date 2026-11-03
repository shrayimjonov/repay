package uz.nodir.notification.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.notification.model.dto.sms.request.SMSSendRequest;
import uz.nodir.notification.model.entity.SMSProtocol;
import uz.nodir.notification.model.enums.TemplateType;
import uz.nodir.notification.model.property.DefaultProperty;
import uz.nodir.notification.service.business.SenderService;
import uz.nodir.notification.service.core.SMSProtocolDAO;
import uz.nodir.notification.service.web.SMSClientService;
import uz.nodir.notification.utils.TextBuilder;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.utils.HttpServletUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class SenderServiceImpl implements SenderService {
    private final SMSClientService smsClientService;
    private final DefaultProperty defaultProperty;
    private final SMSProtocolDAO smsProtocolDAO;

    @Override
    public SimpleResponse<String> send(String text, String phone, TemplateType type, String loanExtId) {
        log.info("Sending text: {} to phone: {}", TextBuilder.hideDigits(text), phone);

        var preparedProtocol = new SMSProtocol(
                text,
                phone,
                type,
                ResponseStatus.CREATED,
                loanExtId,
                HttpServletUtils.getRequestIPAddress()
        );

        var protocol = smsProtocolDAO.save(preparedProtocol);

        var smsRequest = new SMSSendRequest(
                phone,
                text,
                defaultProperty.getProvider()
        );

        var response = smsClientService.send(smsRequest);

        saveToDatabase(protocol, response);

        if (!response.getStatus())
            return new SimpleResponse<>(response.getError().getCode(), response.getError().getMessage());

        if (response.getData().getError() != null)
            return new SimpleResponse<>(response.getData().getError().getCode(), response.getData().getError().getMessage());


        return new SimpleResponse<>(
                response.getData().getResult()
        );
    }

    private void saveToDatabase(SMSProtocol protocol, SimpleResponse<ResultData<String>> response) {
        protocol.setResponse(response.toString().length() < 255 ? response.toString() : response.toString().substring(0, 255));

        if (response.getStatus() && response.getData().getError() == null)
            protocol.setStatus(ResponseStatus.SUCCESS);
        else
            protocol.setStatus(ResponseStatus.FAILED);
        smsProtocolDAO.save(protocol);
    }
}
