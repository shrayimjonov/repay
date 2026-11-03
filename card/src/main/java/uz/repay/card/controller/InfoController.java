package uz.repay.card.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.repay.card.service.business.InfoService;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Payer;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;

@RestController
@RequestMapping("/api/v1/info")
@RequiredArgsConstructor
public class InfoController {
    private final InfoService infoService;

    @GetMapping("/branch")
    public ResponseEntity<?> getBranches() {
        return ResponseEntity.ok(new ResultData<>(Branch.values()));
    }

    @GetMapping("/pc")
    public ResponseEntity<?> getPCs() {
        return ResponseEntity.ok(new ResultData<>(Processing.values()));
    }

    @GetMapping("/service")
    public ResponseEntity<?> getServiceTypes() {
        return ResponseEntity.ok(new ResultData<>(ServiceType.values()));
    }

    @GetMapping("/payer")
    public ResponseEntity<?> getPayers() {
        return ResponseEntity.ok(new ResultData<>(Payer.values()));
    }

    @GetMapping("/p2pInfo")
    public ResponseEntity<?> takeP2PInfo(@RequestParam("pan") String pan) {
        return ResponseEntity.ok(
                infoService.takeP2PInfo(pan)
        );
    }
}
