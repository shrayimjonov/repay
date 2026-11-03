package uz.repay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import uz.repay.card.client.configuration.MasterGatewayClientConfiguration;
import uz.repay.card.model.dto.master.request.*;
import uz.repay.card.model.dto.master.response.*;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.ArrayList;
import java.util.List;

@FeignClient(value = "master-gate", path = "/connect", url = "${external.master-gate.url}", configuration = MasterGatewayClientConfiguration.class)
public interface MasterGatewayFeignClient {

    @PostMapping("/api/v1/verify/otp")
    ResultData<MasterOTPVerifyResponse> otpVerify(MasterBaseRequest<MasterOTPVerifyRequest> request);

    @PostMapping("/api/v1/verify/confirm")
    ResultData<MasterOTPConfirmResponse> otpConfirm(MasterBaseRequest<MasterOTPConfirmRequest> request);

    @PostMapping("/api/v1/card/p2pInfo")
    ResultData<MasterP2PInfoResponse> p2pInfo(MasterBaseRequest<MasterP2PInfoRequest> request);

    @PostMapping("/api/v1/card/balance")
    ResultData<MasterBalanceResponse> balance(MasterBaseRequest<MasterBalanceRequest> request);

    @PostMapping("/api/v1/customer/cards")
    ResultData<List<MasterCustomerCardsResponse>> customerCards(MasterBaseRequest<MasterCustomerCardsRequest> request);

    @PostMapping("/api/v1/customer/cardsByPersonCode")
    ResultData<ArrayList<MasterCustomerCardsResponse>> customerCardsByPersonCode(MasterBaseRequest<MasterCustomerCardsByPINFLRequest> request);

}
