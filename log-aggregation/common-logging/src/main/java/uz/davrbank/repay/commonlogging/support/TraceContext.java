package uz.davrbank.repay.commonlogging.support;

import java.security.SecureRandom;

public final class TraceContext {

    public static final String HDR_TRACE_ID = "X-Trace-Id";
    public static final String HDR_SPAN_ID = "X-Span-Id";

    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID = "spanId";
    public static final String MDC_PARENT_ID = "parentId";

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private TraceContext() {
    }

    public static String newTraceId() {
        return randomHex(32);
    }

    public static String newSpanId() {
        return randomHex(16);
    }

    private static String randomHex(int len) {
        char[] out = new char[len];
        for (int i = 0; i < len; i++) {
            out[i] = HEX[RANDOM.nextInt(16)];
        }
        return new String(out);
    }
}
