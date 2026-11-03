package uz.davrbank.repay.commonlogging.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.davrbank.repay.commonlogging.model.ApiLogEvent;

public class NoOpLogEventPublisher implements LogEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NoOpLogEventPublisher.class);

    public NoOpLogEventPublisher() {
        log.info("common-logging: no Kafka broker configured (spring.kafka.bootstrap-servers) and "
                + "no custom LogEventPublisher bean found — API log events will be discarded. "
                + "Configure a broker or provide your own LogEventPublisher to enable shipping.");
    }

    @Override
    public void publish(ApiLogEvent event) {
        if (log.isTraceEnabled()) {
            log.trace("apilog (discarded): {} {} -> {}",
                    event.getHttpMethod(), event.getEndpoint(), event.getStatusCode());
        }
    }
}
