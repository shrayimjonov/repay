package uz.repay.payment.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@RedisHash(value = "tran")
@AllArgsConstructor
@NoArgsConstructor
public class Tran implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567L;

    @Id
    @Indexed
    private UUID id;

    @Indexed
    private Long transactionId;

    public Tran(Long transactionId) {
        this.id = UUID.randomUUID();
        this.transactionId = transactionId;
    }
}
