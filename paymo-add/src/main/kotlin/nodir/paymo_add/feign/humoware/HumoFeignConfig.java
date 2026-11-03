package nodir.paymo_add.feign.humoware;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;


public class HumoFeignConfig implements RequestInterceptor {

    @Value("${humo.bearer}")
    private String token;


    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    @Bean
    public ErrorDecoder errorCardDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            if (status == 400) {
                String body = "Bad request";
                return getException(response, status, body);
            } else if (status == 404) {
                String body = "No data found";
                return getException(response, status, body);
            } else {
                return new RuntimeException("Response Code " + status);
            }
        };
    }

    @NotNull
    private Exception getException(Response response, int status, String body) {
        try {
            body = IOUtils.toString(response.body().asReader());
        } catch (Exception ignored) {
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        response.headers().forEach((k, v) -> httpHeaders.add("feign-" + k, StringUtils.join(v, ',')));
        return new FeignBadResponseWrapper(status, httpHeaders, body);
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, token));
    }

    @Bean
    public Request.Options HumoCardOptions() {
        return new Request.Options(5000, 5000);
    }

}
