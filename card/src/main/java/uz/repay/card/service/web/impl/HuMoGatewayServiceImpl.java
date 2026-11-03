package uz.repay.card.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.card.client.HuMoGatewayFeignClient;
import uz.repay.card.model.dto.humoGateway.request.HuMoRecoActiveRequest;
import uz.repay.card.model.dto.humoGateway.request.HuMoRecoConfirmRequest;
import uz.repay.card.model.dto.humoGateway.response.HuMoBankBins;
import uz.repay.card.model.dto.humoGateway.response.HuMoRecoActiveResponse;
import uz.repay.card.model.dto.humoGateway.response.HuMoRecoConfirmResponse;
import uz.repay.card.service.web.HuMoGatewayService;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HuMoGatewayServiceImpl implements HuMoGatewayService {
    private final HuMoGatewayFeignClient client;

    @Override
    public SimpleResponse<HuMoRecoActiveResponse> reconciliationActive(HuMoRecoActiveRequest request) {
        log.info("Send to huMo gateway reco active request: {}", request);

        try {
            var response = client.recoActive(request);
            log.info("huMo gateway reco active response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("huMo gateway reco active has error: {}", r.getMessage());
            return new SimpleResponse<>(1001, r.getMessage());
        } catch (FeignException f) {
            log.error("huMo gateway reco active has error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("huMo gateway reco active has error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<HuMoRecoConfirmResponse> reconciliationConfirm(HuMoRecoConfirmRequest request) {
        log.info("Send to huMo gateway reco confirm request: {}", request);

        try {
            var response = client.recoConfirm(request);
            log.info("huMo gateway reco confirm response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("huMo gateway reco confirm has error: {}", r.getMessage());
            return new SimpleResponse<>(1001, r.getMessage());
        } catch (FeignException f) {
            log.error("huMo gateway reco confirm has error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("huMo gateway reco confirm has error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }

    @Override
    public SimpleResponse<List<HuMoBankBins>> getBankBins() {
        log.info("Send to huMo gateway get bank bins request");

        try {
            var response = client.getBankBins();
            log.info("huMo gateway get bank bins response: {}", response);
            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            log.error("huMo gateway get bank bins method has error: {}", r.getMessage());
            return new SimpleResponse<>(1001, r.getMessage());
        } catch (FeignException f) {
            log.error("huMo gateway get bank bins method has error: {}", f.getMessage());
            return new SimpleResponse<>(1002, f.getMessage());
        } catch (Exception e) {
            log.error("huMo gateway get bank bins method has error: {}", e.getMessage());
            return new SimpleResponse<>(1003, e.getMessage());
        }
    }
}
