package uz.repay.card.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.repay.card.model.dto.core.request.card.GuarantorCardInfoRequestDTO;
import uz.repay.card.model.dto.core.request.guarantor.GuarantorCardFinderRequestDTO;
import uz.repay.card.model.dto.core.request.loan.GuarantorCardToggleRequestDTO;
import uz.repay.card.model.dto.core.response.card.CardFinderResponseDTO;
import uz.repay.card.model.dto.core.response.card.CardInfoResponseDTO;
import uz.repay.card.service.business.GuarantorCardService;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;


@RestController
@RequestMapping(value = "/api/v1/guarantorCard", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "guarantor-card-endpoint")
public class GuarantorCardController {
    private final GuarantorCardService service;

    @PostMapping("/info")
    @Operation(summary = "Guarantor card info", description = "Get guarantor card info", operationId = "guarantor-card-info")
    public ResponseEntity<ResultData<CardInfoResponseDTO>> cardInfo(@RequestBody @Valid GuarantorCardInfoRequestDTO requestDTO) {
        return ResponseEntity.ok(service.getCardInfo(requestDTO));
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).REMOVE_CARD)")
    @Operation(summary = "Toggle guarantor card", description = "Change card status to delete or approve", operationId = "guarantor-card-toggle")
    public ResponseEntity<ResultData<CoreError>> toggleCard(@RequestBody @Valid GuarantorCardToggleRequestDTO dto) {
        return ResponseEntity.ok(service.deleteOrApproveCard(dto));
    }

    @PostMapping("/findCards")
    @PreAuthorize("hasRole(T(uz.repay.common.model.enums.UserPermission).FIND_NEW_CARDS)")
    @Operation(summary = "Find guarantor cards", description = "Find guarantor cards and add", operationId = "guarantor-card-finder")
    public ResponseEntity<ResultData<CardFinderResponseDTO>> findCards(@RequestBody @Valid GuarantorCardFinderRequestDTO requestDTO) {
        return ResponseEntity.ok(service.findGuarantorCards(requestDTO));
    }

}
