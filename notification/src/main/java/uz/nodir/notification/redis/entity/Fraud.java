package uz.nodir.notification.redis.entity;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @group: DavrCoders
 **/

@Getter
@Setter
@ToString
@RedisHash(value = "notification_fraud", timeToLive = 300) // time to live value in seconds
@AllArgsConstructor
@NoArgsConstructor
public class Fraud implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Indexed
    private UUID id;

    @NotNull
    @Indexed
    private String phone;

    @NotNull
    @Indexed
    private String loanExtId;

}
