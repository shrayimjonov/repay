package uz.repay.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Slf4j
public class HttpServletUtils {

    public static HttpServletRequest getCurrentHttpRequest() {
        return
                ((ServletRequestAttributes) Objects.requireNonNull(
                        RequestContextHolder.getRequestAttributes())).getRequest();
    }

    public static String getRequestIPAddress() {
        var requestContext = RequestContextHolder.getRequestAttributes();

        if (requestContext == null)
            return "127.0.0.1";


        String realIp = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                .getHeader("X-Real-IP");
        String forwardedFor = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                .getHeader("X-Forwarded-For");

        log.info("Request real-ip address: {}", realIp);
        log.info("Request forwarded for address: {}", forwardedFor);
        return realIp;
    }


    public static HttpServletResponse getCurrentHttpResponse() {
        return
                ((ServletRequestAttributes) Objects.requireNonNull(
                        RequestContextHolder.getRequestAttributes())).getResponse();
    }

    public static String takeToken() {
        return getCurrentHttpRequest().getHeader("Authorization");
    }
}
