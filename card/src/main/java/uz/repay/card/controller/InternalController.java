package uz.repay.card.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.repay.card.model.dto.core.request.card.GetCardByTokenRequest;
import uz.repay.card.model.dto.core.request.loan.LoanCardsV2DTO;
import uz.repay.card.model.dto.core.response.card.GetCardByTokenResponse;
import uz.repay.card.service.business.EPosService;
import uz.repay.card.service.business.InfoService;
import uz.repay.card.service.business.LoanCardService;
import uz.repay.card.service.business.StatusService;
import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;

@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
public class InternalController {
    private final LoanCardService loanCardService;
    private final EPosService ePosService;
    private final InfoService infoService;
    private final StatusService statusService;

    @PostMapping("/loansCards")
    public ResponseEntity<?> getLoansCards(@RequestBody @Valid LoanCardsV2DTO requestDTO) {
        return ResponseEntity.ok(loanCardService.getLoansCardsV2(requestDTO));
    }

    @GetMapping("/ePos")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(ePosService.getAll());
    }

    @PostMapping("/cardByPan")
    public ResponseEntity<?> getCardByPan(@RequestBody @Valid CardFullInfoRequestDTO requestDTO) {
        return ResponseEntity.ok(infoService.getCardByPan(requestDTO));
    }

    @PostMapping("/get/pan")
    public ResponseEntity<GetCardByTokenResponse> getCardByToken(@RequestBody @Valid GetCardByTokenRequest requestDTO) {
        return ResponseEntity.ok(infoService.getCardByToken(requestDTO));//test
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok(statusService.findAll());
    }

    @DeleteMapping("/deactivateCard/{cardId}")
    public ResponseEntity<?> deactivateCard(@PathVariable("cardId") Long cardId) {
        return ResponseEntity.ok(infoService.deActivateCard(cardId));
    }

}
