package uz.nodir.repayotp.config;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.keycloak.provider.ProviderConfigProperty.PASSWORD;
import static org.keycloak.provider.ProviderConfigProperty.STRING_TYPE;

public class IpTrustAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "repay-trusted-ip-authenticator";


    @Override
    public String getDisplayType() {
        return "Repay Trusted IP confirmation";
    }

    @Override
    public String getReferenceCategory() {
        return "otp";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Привязка IP на логин";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property("smtpHost", "SMTP Host", "SMTP сервер для отправки писем", STRING_TYPE, "127.0.0.1", Collections.emptyList(), false)
                .property("smtpPort", "SMTP Port", "Порт SMTP сервера", STRING_TYPE, "25", Collections.emptyList(), false)
                .property("smtpUser", "SMTP User", "SMTP логин", STRING_TYPE, "", Collections.emptyList(), false)
                .property("smtpPassword", "SMTP Password", "SMTP пароль", PASSWORD, "", Collections.emptyList(), false)
                .property("smtpAuthType", "SMTP Authentication Type", "Тип аутентификации для SMTP",
                        ProviderConfigProperty.LIST_TYPE, "TLS", Arrays.asList("PLAIN", "SSL", "TLS"), false)
                .build();
    }


    @Override
    public Authenticator create(KeycloakSession session) {
        return new RepayEmailOtpAuthenticator();
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }


    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
