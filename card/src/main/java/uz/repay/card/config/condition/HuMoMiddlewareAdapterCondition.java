package uz.repay.card.config.condition;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import uz.repay.card.model.enums.HuMoAdapter;

import java.util.Objects;


@Slf4j
public class HuMoMiddlewareAdapterCondition implements Condition {
    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {

        String propertyName = "internal.humo.adapter";

        Environment environment = context.getEnvironment();
        boolean b = environment.containsProperty(propertyName);

        log.info("Detect humo adapter property from yml: {} is result: {}", propertyName, b);
        if (!b)
            return false;
        else {
            log.info("huMo adapter condition is: {}", environment.getProperty(propertyName));
            return Objects.equals(environment.getProperty(propertyName), HuMoAdapter.middleware.name());
        }

    }
}
