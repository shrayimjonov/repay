package uz.repay.card.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.repay.card.model.dto.core.request.loan.LoanCardsV2DTO;
import uz.repay.card.model.dto.humo.request.HumoCardInfoRequestDTO;
import uz.repay.card.model.dto.humo.response.HumoCardInfoResponseList;
import uz.repay.card.service.business.LoanCardV2Service;
import uz.repay.card.service.business.adapter.CardInfoService;
import uz.repay.common.model.dto.response.core.ResultData;

@Slf4j
@RestController
@RequestMapping("/api/v2/internal")
@RequiredArgsConstructor
public class InternalV2Controller {
    private final LoanCardV2Service loanCardV2Service;
    private final CardInfoService cardInfoService;

    @PostMapping("/loansCards")
    public ResponseEntity<?> getLoansCards(@RequestBody @Valid LoanCardsV2DTO requestDTO) {
        return ResponseEntity.ok(loanCardV2Service.getLoansCards(requestDTO));
    }

    @GetMapping("/card-info/{loanId}")
    public ResponseEntity<?> getCardInfo(@PathVariable Long loanId) {
        log.info("Get card info for loanId: {}", loanId);
        return ResponseEntity.ok(cardInfoService.cardInfo(loanId));
    }

    @PostMapping("/card-info/humo")
    public ResponseEntity<ResultData<HumoCardInfoResponseList>> getHumoCardInfo(@Valid @RequestBody HumoCardInfoRequestDTO requestDTO) {
        return ResponseEntity.ok(cardInfoService.getHumoCardsInfoByLoanExtIds(requestDTO));
    }
}