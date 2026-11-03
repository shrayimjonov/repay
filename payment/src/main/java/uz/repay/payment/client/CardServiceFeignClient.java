package uz.repay.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;
import uz.repay.common.model.dto.request.card.response.CardFullInfoResponseDTO;
import uz.repay.common.model.dto.request.card.response.StatusResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.payment.client.configuration.CardServiceFeignConfiguration;
import uz.repay.payment.model.dto.cardService.response.TerminalResponse;
import uz.repay.payment.model.dto.uzcard.request.GetCardByTokenRequest;
import uz.repay.payment.model.dto.uzcard.response.GetCardByTokenResponse;

import java.util.List;


@FeignClient(name = "card", path = "/card", configuration = CardServiceFeignConfiguration.class)
public interface CardServiceFeignClient {
    @GetMapping("/api/v1/internal/ePos")

    ResultData<List<TerminalResponse>> getAllEPosses();

    @PostMapping("/api/v1/internal/cardByPan")
    ResultData<CardFullInfoResponseDTO> getCardFullInfoByPan(CardFullInfoRequestDTO requestDTO);

    @GetMapping("/api/v1/internal/status")
    ResultData<List<StatusResponseDTO>> findAllStatus();

    @PostMapping("/api/v1/internal/get/pan")
    GetCardByTokenResponse getCardByToken(@RequestBody GetCardByTokenRequest request);
}