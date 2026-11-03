package uz.repay.payment.redis.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@RedisHash(value = "switcher")
@AllArgsConstructor
public class Switcher implements Serializable {

    @Id
    @Indexed
    private UUID id;

    @NonNull
    @Indexed
    private String name;

    @NonNull
    private Boolean isEnabled;


}
