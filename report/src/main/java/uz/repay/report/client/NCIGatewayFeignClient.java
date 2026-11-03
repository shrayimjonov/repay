package uz.repay.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanGraphResponse;
import uz.repay.report.client.configuration.NCIGatewayClientConfiguration;

import java.util.List;

@FeignClient(name = "nci-gateway", url = "${external.nci-gateway.url}", configuration = NCIGatewayClientConfiguration.class)
public interface NCIGatewayFeignClient {

    @PostMapping("/loan/graph")
    ResultData<List<LoanGraphResponse>> getLoanGraph(LoanSimpleRequestDTO request);


}
