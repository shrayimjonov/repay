package uz.davrbank.repay.commonlogging.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.davrbank.repay.commonlogging.support.TraceContext;

import java.io.IOException;

public class TraceContextFilter extends OncePerRequestFilter implements Ordered {

    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE;

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String traceId = firstNonBlank(req.getHeader(TraceContext.HDR_TRACE_ID), TraceContext.newTraceId());
        String parentSpanId = req.getHeader(TraceContext.HDR_SPAN_ID);
        String spanId = TraceContext.newSpanId();

        MDC.put(TraceContext.MDC_TRACE_ID, traceId);
        MDC.put(TraceContext.MDC_SPAN_ID, spanId);
        if (StringUtils.hasText(parentSpanId)) {
            MDC.put(TraceContext.MDC_PARENT_ID, parentSpanId);
        }
        res.setHeader(TraceContext.HDR_TRACE_ID, traceId);
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove(TraceContext.MDC_TRACE_ID);
            MDC.remove(TraceContext.MDC_SPAN_ID);
            MDC.remove(TraceContext.MDC_PARENT_ID);
        }
    }

    private static String firstNonBlank(String a, String b) {
        return StringUtils.hasText(a) ? a : b;
    }
}
