package uz.repay.report.client.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uz.repay.common.utils.HttpServletUtils;

@Component
@RequiredArgsConstructor
public class LoanBearerRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        final var token = HttpServletUtils.takeToken();

        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servletRequestAttributes) {
            template.header(HttpHeaders.AUTHORIZATION, token);
        }
    }
}