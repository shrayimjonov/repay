//package uz.repay.autodebit.controller;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import uz.repay.autodebit.model.core.request.AutoPaymentSwitcherDTO;
//import uz.repay.autodebit.service.business.AutoPaymentServiceV3;
//import uz.repay.common.model.dto.response.core.ResultData;
//
///**
// */
//@RestController
//@RequestMapping("/api/v3/control")
//@RequiredArgsConstructor
//public class AutoPayV3Controller {
//    private final AutoPaymentServiceV3 autoPaymentServiceV3;
//
//    @PatchMapping("/turn")
//    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).AUTO_PAYMENT_ENABLE)")
//    public ResponseEntity<?> turnOffAutoPayment(@RequestBody @Valid AutoPaymentSwitcherDTO dto) {
//        autoPaymentServiceV3.switchAP(dto);
//        return ResponseEntity.ok(
//                new ResultData<>(dto.getEnable())
//        );
//    }
//}
