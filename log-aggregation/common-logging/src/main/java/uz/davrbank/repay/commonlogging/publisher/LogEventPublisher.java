package uz.davrbank.repay.commonlogging.publisher;

import uz.davrbank.repay.commonlogging.model.ApiLogEvent;


public interface LogEventPublisher {

    void publish(ApiLogEvent event);
}
