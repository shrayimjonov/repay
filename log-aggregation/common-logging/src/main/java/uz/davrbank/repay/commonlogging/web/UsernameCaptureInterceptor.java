package uz.davrbank.repay.commonlogging.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.Principal;

public class UsernameCaptureInterceptor implements HandlerInterceptor {

    public static final String USERNAME_ATTR = "app.username";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Principal p = request.getUserPrincipal();
        if (p != null && StringUtils.hasText(p.getName())) {
            request.setAttribute(USERNAME_ATTR, p.getName());
        }
        return true;
    }
}