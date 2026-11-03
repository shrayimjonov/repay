package uz.davrbank.repay.commonlogging.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import uz.davrbank.repay.commonlogging.config.LoggingProperties;
import uz.davrbank.repay.commonlogging.model.ApiLogEvent;
import uz.davrbank.repay.commonlogging.model.Direction;
import uz.davrbank.repay.commonlogging.publisher.LogEventPublisher;
import uz.davrbank.repay.commonlogging.support.ClientIpResolver;
import uz.davrbank.repay.commonlogging.support.SuccessResolver;
import uz.davrbank.repay.commonlogging.support.TraceContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ApiLogFilter extends OncePerRequestFilter implements Ordered {
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 1;

    private final LogEventPublisher publisher;
    private final LoggingProperties props;
    private final ZoneId zone;

    public ApiLogFilter(LogEventPublisher publisher, LoggingProperties props) {
        this.publisher = publisher;
        this.props = props;
        this.zone = ZoneId.of(props.getZone());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain) throws ServletException, IOException {

        ContentCachingRequestWrapper reqW =
                new ContentCachingRequestWrapper(req, props.getMaxBodyBytes());
        ContentCachingResponseWrapper resW = new ContentCachingResponseWrapper(res);

        long start = System.nanoTime();
        String error = null;
        try {
            chain.doFilter(reqW, resW);
        } catch (Exception ex) {
            error = ex.getMessage();
            log.error("ERROR on filter: {}", error);
            throw ex;
        } finally {
            try {
                if (props.isCaptureBodies() && reqW.getContentAsByteArray().length == 0) {
                    try {
                        reqW.getInputStream().readAllBytes();
                    } catch (IOException ignore) {
                    }
                }
                int status = resW.getStatus();
                if (error == null) {
                    Object attr = req.getAttribute("app.errorMessage");
                    error = attr != null ? attr.toString() : null;
                }
                publisher.publish(ApiLogEvent.builder()
                        .eventTime(ZonedDateTime.now(zone))
                        .traceId(MDC.get(TraceContext.MDC_TRACE_ID))
                        .spanId(MDC.get(TraceContext.MDC_SPAN_ID))
                        .parentSpanId(MDC.get(TraceContext.MDC_PARENT_ID))
                        .direction(Direction.IN)
                        .service(props.getService())
                        .username(currentUsername(reqW))
                        .clientIp(resolveClientIp(reqW))
                        .mac_address(reqW.getHeader(props.getMacHeader()))
                        .endpoint(routeTemplate(reqW))
                        .params(extractParams(reqW))
                        .httpMethod(reqW.getMethod())
                        .statusCode(status)
                        .success(SuccessResolver.of(status, error))
                        .durationMs((System.nanoTime() - start) / 1_000_000)
                        .attempt(1)
                        .errorMessage(error)
                        .requestBody(captureBody(reqW.getContentAsByteArray(), reqW.getContentType()))
                        .responseBody(captureBody(resW.getContentAsByteArray(), resW.getContentType()))
                        .build());
            } catch (Exception loggingFailure) {
                log.error("apilog capture failed: {}", loggingFailure.getMessage());
            } finally {
                resW.copyBodyToResponse();
            }
        }
    }

    private Map<String, String> extractParams(HttpServletRequest req) {
        Map<String, String> out = new LinkedHashMap<>();
        req.getParameterMap().forEach((k, v) -> out.put(k, String.join(",", v)));
        return out;
    }

    private String captureBody(byte[] buf, String contentType) {
        if (!props.isCaptureBodies() || buf == null || buf.length == 0) {
            return null;
        }
        if (contentType == null || !contentType.contains("application/json")) {
            return null;
        }
        int len = Math.min(buf.length, props.getMaxBodyBytes());
        return new String(buf, 0, len, StandardCharsets.UTF_8);
    }

    private String routeTemplate(HttpServletRequest req) {
        Object best = req.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return best != null ? best.toString() : req.getRequestURI();
    }

    private String resolveClientIp(HttpServletRequest req) {
        return ClientIpResolver.resolve(req);
    }

    private String currentUsername(HttpServletRequest req) {
        Object attr = req.getAttribute(UsernameCaptureInterceptor.USERNAME_ATTR);
        if (attr instanceof String s && StringUtils.hasText(s)) {
            return s;
        }
        Principal p = req.getUserPrincipal();
        return (p != null && StringUtils.hasText(p.getName())) ? p.getName() : "anonymous";
    }
}
