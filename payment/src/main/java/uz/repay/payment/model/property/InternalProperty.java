package uz.repay.payment.model.property;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "internal", ignoreUnknownFields = false, ignoreInvalidFields = false)
public class InternalProperty {
    private FraudProperty fraud;
    private TemporaryProperty temporary;
    private PaymentProperty payment;
    private CardProperty card;

    @Data
    public static class FraudProperty {
        private Integer payCount;
    }

    @Data
    public static class TemporaryProperty {
        private String intervalInMinutes;
    }

    @Data
    public static class PaymentProperty {
        private Integer successTime;
        private Integer disabledTime;
        private String externalPrefix;
    }

    @Data
    public static class CardProperty {
        private Integer disabledTime;
    }

}
