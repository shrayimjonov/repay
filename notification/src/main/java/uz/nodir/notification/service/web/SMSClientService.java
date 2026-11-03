package uz.nodir.notification.service.web;

import uz.nodir.notification.model.dto.sms.request.SMSSendRequest;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;

public interface SMSClientService {

    SimpleResponse<ResultData<String>> send(SMSSendRequest request);
}
