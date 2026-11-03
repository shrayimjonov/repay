package uz.davrbank.repay.commonlogging.publisher;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import uz.davrbank.repay.commonlogging.model.ApiLogEvent;

public class KafkaLogEventPublisher implements LogEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaLogEventPublisher.class);
    private static final String KAFKA_LOGGING_TOPIC_NAME = "api-log";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Counter dropped;
    private final Counter published;

    public KafkaLogEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                  MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.dropped = meterRegistry != null ? meterRegistry.counter("apilog.dropped") : null;
        this.published = meterRegistry != null ? meterRegistry.counter("apilog.published") : null;
    }

    @Override
    public void publish(ApiLogEvent event) {
        try {
            kafkaTemplate.send(KAFKA_LOGGING_TOPIC_NAME, event.getTraceId(), event)
                    .whenComplete((r, ex) -> {
                        if (ex != null) {
                            increment(dropped);
                        } else {
                            increment(published);
                        }
                    });
        } catch (Exception e) {
            increment(dropped);
            log.debug("apilog send failed, dropped: {}", e.getMessage());
        }
    }

    private static void increment(Counter counter) {
        if (counter != null) {
            counter.increment();
        }
    }
}
