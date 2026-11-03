package uz.repay.payment.redis.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.utils.DateUtils;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@RedisHash(value = "fraud", timeToLive = 86400)
@AllArgsConstructor
@NoArgsConstructor
public class Fraud implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567L;

    @Id
    @Indexed
    private UUID id;

    @Indexed
    private String loanId;

    private LocalDateTime localDateTime;

    private String username;

    @Indexed
    private String pan;

    private Long amount;

    public Fraud(PayRequestDTO requestDTO) {
        this.id = UUID.randomUUID();
        this.loanId = requestDTO.getLoanExtId();
        this.localDateTime = DateUtils.getDateTimeNow();
        this.username = SecurityUtils.getUsername();
        this.pan = requestDTO.getParams().getCardPan();
        this.amount = requestDTO.getAmount();
    }
}
