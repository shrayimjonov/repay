package uz.repay.card.model.dto.core.response.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.card.model.entity.LoanCard;
import uz.repay.card.model.entity.base.BaseCardEntity;
import uz.repay.common.model.enums.AddType;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoResponseDTO {
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
    private String createdBy;
    private LocalDateTime updateTime;
    private String ipAddress;
    private AddType addType;

    public CardInfoResponseDTO(BaseCardEntity baseCard) {
        var card = baseCard.getCard();
        this.id = card.getId();
        this.maskedPan = card.getMaskedPan();
        this.holder = card.getHolder();
        this.processing = card.getProcessing();
        this.phone = card.getPhone();
        this.isConfirmed = baseCard.getIsConfirmed();
        this.token = card.getToken();
        this.service = card.getService();
        this.isActive = card.getIsActive();
        this.initTime = baseCard.getInitTime();
        this.createdBy = baseCard.getCreatedBy();
        this.updateTime = baseCard.getUpdateTime();
        this.ipAddress = baseCard.getIpAddress();
        this.addType = baseCard.getAddType();
    }


}
