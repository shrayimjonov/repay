package uz.repay.card.redis.entity;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import uz.repay.common.model.enums.Processing;

import java.io.Serializable;

@Getter
@Setter
@ToString
@RedisHash(value = "message", timeToLive = 120L)
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    @Id
    @Indexed
    private String id;

    @NonNull
    @Indexed
    private String phone;

    @NonNull
    @Indexed
    private String code;

    @NonNull
    @Indexed
    private String card;

    @NonNull
    private Processing processing;

    @NonNull
    private Long loanId;

    @NotNull
    private String loanExtId;


    public Message(String id, @NonNull String phone, @NonNull String code, @NonNull String card, @NonNull Processing processing, @NonNull Long loanId) {
        this.id = id;
        this.phone = phone;
        this.code = code;
        this.card = card;
        this.processing = processing;
        this.loanId = loanId;
    }
}
