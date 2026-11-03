package uz.repay.common.model.dto.request.card.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardFullInfoResponseDTO {
    private Long id;
    private String maskedPan;
    private String holder;
    private Processing processing;
    private String phone;
    private Boolean isConfirmed;
    private String token;
    private ServiceType service;
    private Boolean isActive;
    private LocalDateTime initTime;
    private String expiry;
    private String createdBy;
    private LocalDateTime updateTime;

    public CardFullInfoResponseDTO(Long id, String maskedPan,
                                   String holder, Processing processing,
                                   String phone, Boolean isConfirmed,
                                   String token, ServiceType service,
                                   Boolean isActive, String expiry,
                                   LocalDateTime initTime, String createdBy,
                                   LocalDateTime updateTime) {
        this.id = id;
        this.maskedPan = maskedPan;
        this.holder = holder;
        this.processing = processing;
        this.phone = phone;
        this.isConfirmed = isConfirmed;
        this.token = token;
        this.service = service;
        this.isActive = isActive;
        this.expiry = expiry;
        this.initTime = initTime;
        this.createdBy = createdBy;
        this.updateTime = updateTime;
    }
}
