package uz.repay.card.service.web;

import uz.repay.card.model.dto.humoGateway.request.HuMoRecoActiveRequest;
import uz.repay.card.model.dto.humoGateway.request.HuMoRecoConfirmRequest;
import uz.repay.card.model.dto.humoGateway.response.HuMoBankBins;
import uz.repay.card.model.dto.humoGateway.response.HuMoRecoActiveResponse;
import uz.repay.card.model.dto.humoGateway.response.HuMoRecoConfirmResponse;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.List;

public interface HuMoGatewayService {

    SimpleResponse<HuMoRecoActiveResponse> reconciliationActive(HuMoRecoActiveRequest request);

    SimpleResponse<HuMoRecoConfirmResponse> reconciliationConfirm(HuMoRecoConfirmRequest request);

    SimpleResponse<List<HuMoBankBins>> getBankBins();

}
