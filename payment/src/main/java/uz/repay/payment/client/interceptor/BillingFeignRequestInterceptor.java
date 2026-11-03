package uz.repay.payment.client.interceptor;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uz.repay.payment.model.dto.billing.keycloak.request.TokenRequest;
import uz.repay.payment.model.dto.billing.keycloak.response.TokenResponse;
import uz.repay.payment.model.property.BillingProperty;


@Slf4j
public class BillingFeignRequestInterceptor implements RequestInterceptor {
    private final RestTemplate restTemplate;
    private final BillingProperty billingProperty;
    private TokenResponse token;
    private final TokenRequest tokenRequest;
    private long expireTimeToken;

    public BillingFeignRequestInterceptor(BillingProperty billingProperty, RestTemplate restTemplate) {
        this.tokenRequest = new TokenRequest(billingProperty.getKeycloak());
        this.restTemplate = restTemplate;
        this.billingProperty = billingProperty;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Content-Type", "application/json");
        requestTemplate.header("Accept", "application/json");
        requestTemplate.header("Authorization", "Bearer " + getAccessToken());
    }

    private String getAccessToken() {
        if (token == null || isExpired()) {
            requestNewToken();
        }
        return token.getAccess_token();
    }

    private boolean isExpired() {
        long ts = System.currentTimeMillis();
        log.debug("Billing expTime {}, Current TS {}", expireTimeToken, ts);
        return expireTimeToken - 60000 <= ts;
    }

    private void requestNewToken() {
        log.debug("Request new Access token for Billing");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(tokenRequest.toMap(), headers);
        token = restTemplate.postForEntity(billingProperty.getKeycloak().getUrl(), request, TokenResponse.class).getBody();
        expireTimeToken = System.currentTimeMillis() + token.getExpires_in() * 1000L;
    }
}
