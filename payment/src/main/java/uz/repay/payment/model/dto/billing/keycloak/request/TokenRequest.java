package uz.repay.payment.model.dto.billing.keycloak.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uz.repay.payment.model.property.BillingProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String grantType;
    private String scope;

    public TokenRequest(BillingProperty.Keycloak keycloak) {
        this.username = keycloak.getUsername();
        this.password = keycloak.getPassword();
        this.clientId = keycloak.getClientId();
        this.clientSecret = keycloak.getClientSecret();
        this.grantType = keycloak.getGrantType();
        this.scope = keycloak.getScope();
    }

    public MultiValueMap<String, String> toMap() {
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        result.add("username", username);
        result.add("password", password);
        result.add("client_id", clientId);
        result.add("client_secret", clientSecret);
        result.add("grant_type", grantType);
        result.add("scope", scope);
        return result;
    }
}
