package uz.repay.payment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.AmountType;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.payment.model.dto.cardService.response.TerminalResponse;
import uz.repay.payment.model.dto.core.info.*;
import uz.repay.payment.model.enums.ABSDocType;
import uz.repay.payment.model.enums.ABSPayType;
import uz.repay.payment.model.enums.PurposeValues;
import uz.repay.payment.service.business.component.CommonHelper;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/info", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "info_endpoints", description = "payment info endpoints")
public class InfoController {

    private final CommonHelper commonHelper;

    @GetMapping("/purposeValues")
    public ResponseEntity<ResultData<List<PurposeValueResponseDTO>>> getPurposeValues() {
        return ResponseEntity.ok(
                ResultData.ok(
                        Arrays.stream(PurposeValues
                                        .values())
                                .map(PurposeValueResponseDTO::new)
                                .toList()
                )
        );
    }

    @GetMapping("/currency")
    public ResponseEntity<ResultData<List<CurrencyResponseDTO>>> getCurrencies() {
        return ResponseEntity.ok(
                ResultData.ok(
                        Arrays.stream(Currency
                                        .values())
                                .map(CurrencyResponseDTO::new)
                                .toList()
                )
        );
    }


    @GetMapping("/absDocTypes")
    public ResponseEntity<ResultData<List<ABSDocTypeResponseDTO>>> getABSDocTypes() {
        return ResponseEntity.ok(
                ResultData.ok(
                        Arrays.stream(ABSDocType
                                        .values())
                                .map(ABSDocTypeResponseDTO::new)
                                .toList()
                )
        );
    }


    @GetMapping("/absPayTypes")
    public ResponseEntity<ResultData<List<ABSPayTypeResponseDTO>>> getABSPayTypes() {
        return ResponseEntity.ok(
                ResultData.ok(
                        Arrays.stream(ABSPayType
                                        .values())
                                .map(ABSPayTypeResponseDTO::new)
                                .toList()
                )
        );
    }


    @GetMapping("/branch")
    public ResponseEntity<ResultData<List<BranchResponseDTO>>> getBranches() {
        return ResponseEntity.ok(
                ResultData.ok(
                        Arrays.stream(Branch
                                        .values())
                                .map(BranchResponseDTO::new)
                                .toList()
                )
        );
    }

    @GetMapping("/status")
    public ResponseEntity<ResultData<List<StatusResponseDTO>>> getStatuses() {
        return ResponseEntity.ok(
                ResultData.ok(
                        Arrays.stream(ResponseStatus.values())
                                .map(StatusResponseDTO::new)
                                .toList()
                )
        );
    }


    @GetMapping("/amountTypes")
    public ResponseEntity<ResultData<List<AmountTypeResponseDTO>>> getAmountTypes() {
        return ResponseEntity.ok(
                ResultData.ok(
                        Arrays.stream(AmountType.values())
                                .map(AmountTypeResponseDTO::new)
                                .toList()
                )
        );
    }

    @GetMapping("/epos")
    public ResponseEntity<SimpleResponse<TerminalResponse>> getEposForUzcardPayment(@RequestBody EposCheckDTO checkDTO) {
        return ResponseEntity.ok(commonHelper.getEPosForUzcardPayment(checkDTO.processing(), checkDTO.branch(), checkDTO.forTransPay()));
    }

}
