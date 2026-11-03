package uz.repay.payment.service.business;

import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.payment.NCICourseResponse;

import java.util.List;

public interface InfoService {
    ResultData<List<NCICourseResponse>> getCourse();
}
