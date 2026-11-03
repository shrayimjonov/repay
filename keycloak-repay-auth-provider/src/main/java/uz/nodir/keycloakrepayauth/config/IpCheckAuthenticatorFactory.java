package uz.nodir.keycloakrepayauth.config;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

import static org.keycloak.provider.ProviderConfigProperty.STRING_TYPE;

public class IpCheckAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "repay-ip-check-authenticator";

    @Override
    public String getDisplayType() {
        return "Repay IP authentication";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[] {
                AuthenticationExecutionModel.Requirement.REQUIRED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Проверяет IP-адрес пользователя на логин";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        ProviderConfigProperty name = new ProviderConfigProperty();

        name.setType(STRING_TYPE);
        name.setName("One_IP_One_Token_One_Time");
        name.setLabel("IP Address from which sign ins are allowed");
        name.setHelpText("Only accepts IP addresses");

        return Collections.singletonList(name);
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new IpCheckRepayAuthenticator();
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
