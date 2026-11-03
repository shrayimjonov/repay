package uz.repay.autodebit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.repay.autodebit.client.configuration.CardServiceFeignConfiguration;
import uz.repay.autodebit.model.card.request.HumoCardInfoRequestDTO;
import uz.repay.autodebit.model.card.request.LoanCardsRequestDTO;
import uz.repay.autodebit.model.card.response.HumoCardInfoResponseList;
import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;
import uz.repay.common.model.dto.request.card.response.CardFullInfoResponseDTO;
import uz.repay.common.model.dto.response.cardService.EPosResponseDTO;
import uz.repay.common.model.dto.response.core.LoansCardsResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.result.LoanCardsResult;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name = "card", path = "/card", configuration = CardServiceFeignConfiguration.class)
public interface CardFeignClient {

    @PostMapping("/api/v1/internal/loansCards")
    ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> getLoansCards(LoanCardsRequestDTO requestDTO);

    @GetMapping("/api/v1/internal/ePos")
    ResultData<List<EPosResponseDTO>> getAllEPosses();

    @DeleteMapping("/api/v1/internal/deactivateCard/{cardId}")
    ResultData<Boolean> deactivateCard(@PathVariable("cardId") Long cardId);

    @PostMapping("/api/v2/internal/loansCards")
    ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> getLoansCardsV2(LoanCardsRequestDTO requestDTO);

    @PostMapping("/api/v2/internal/card-info/humo")
    ResultData<HumoCardInfoResponseList> getHumoCardsInfoByLoanExtIds(@RequestBody HumoCardInfoRequestDTO requestDTO);

    @PostMapping("/api/v1/internal/cardByPan")
    ResultData<CardFullInfoResponseDTO> getCardFullInfoByPan(CardFullInfoRequestDTO requestDTO);
}
