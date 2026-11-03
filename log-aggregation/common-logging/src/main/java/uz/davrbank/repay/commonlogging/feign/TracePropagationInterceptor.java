package uz.davrbank.repay.commonlogging.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import uz.davrbank.repay.commonlogging.support.TraceContext;

public class TracePropagationInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String traceId = MDC.get(TraceContext.MDC_TRACE_ID);
        if (traceId == null) {
            traceId = TraceContext.newTraceId();
            MDC.put(TraceContext.MDC_TRACE_ID, traceId);
        }
        String outSpan = TraceContext.newSpanId();

        template.removeHeader(TraceContext.HDR_TRACE_ID);
        template.removeHeader(TraceContext.HDR_SPAN_ID);
        template.header(TraceContext.HDR_TRACE_ID, traceId);
        template.header(TraceContext.HDR_SPAN_ID, outSpan);
    }
}
