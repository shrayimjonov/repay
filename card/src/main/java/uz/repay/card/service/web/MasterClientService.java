package uz.repay.card.service.web;

import uz.repay.card.model.dto.master.request.*;
import uz.repay.card.model.dto.master.response.*;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.ArrayList;
import java.util.List;

public interface MasterClientService {
    SimpleResponse<ResultData<MasterOTPVerifyResponse>> otpVerify(MasterOTPVerifyRequest request);

    SimpleResponse<ResultData<MasterOTPConfirmResponse>> otpConfirm(MasterOTPConfirmRequest request);

    SimpleResponse<ResultData<MasterP2PInfoResponse>> p2pInfo(MasterP2PInfoRequest request);

    SimpleResponse<ResultData<MasterBalanceResponse>> balance(MasterBalanceRequest request);

    SimpleResponse<ResultData<List<MasterCustomerCardsResponse>>> customerCards(MasterCustomerCardsRequest request);

    SimpleResponse<ResultData<ArrayList<MasterCustomerCardsResponse>>> customerCardsByPersonCode(MasterCustomerCardsByPINFLRequest request);
}
