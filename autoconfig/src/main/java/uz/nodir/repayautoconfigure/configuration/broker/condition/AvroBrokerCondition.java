package uz.nodir.repayautoconfigure.configuration.broker.condition;

import lombok.NonNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import uz.nodir.repayautoconfigure.model.enums.BrokerType;
import uz.nodir.repayautoconfigure.model.property.BrokerProperty;


@EnableConfigurationProperties(value = BrokerProperty.class)
public class AvroBrokerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        String brokerType = context.getEnvironment().getProperty("broker.type");
        return BrokerType.AVRO.name().equalsIgnoreCase(brokerType);
    }
}
