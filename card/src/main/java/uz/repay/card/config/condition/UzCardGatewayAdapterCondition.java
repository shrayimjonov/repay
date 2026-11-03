package uz.repay.card.config.condition;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import uz.repay.card.model.enums.UzCardAdapter;

import java.util.Objects;


@Slf4j
public class UzCardGatewayAdapterCondition implements Condition {
    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {

        String propertyName = "internal.uzcard.adapter";

        Environment environment = context.getEnvironment();
        boolean b = environment.containsProperty(propertyName);

        log.info("Detect uzCard adapter property from yml: {} is result: {}", propertyName, b);
        if (!b)
            return false;
        else {
            log.info("UzCard adapter condition is: {}", environment.getProperty(propertyName));
            return Objects.equals(environment.getProperty(propertyName), UzCardAdapter.gateway.name());
        }

    }
}
