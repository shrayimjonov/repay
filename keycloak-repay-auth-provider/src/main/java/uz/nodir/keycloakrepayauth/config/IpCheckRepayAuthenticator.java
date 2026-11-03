package uz.nodir.keycloakrepayauth.config;


import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.nodir.keycloakrepayauth.service.UserLoginProtocolComponent;

import java.util.List;




public class IpCheckRepayAuthenticator implements Authenticator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserLoginProtocolComponent userLoginProtocolComponent = new UserLoginProtocolComponent();


    @Override
    public void authenticate(AuthenticationFlowContext context) {

     //   String userIp = context.getConnection().getRemoteAddr();

        var headers = context.getHttpRequest().getHttpHeaders();
        var requestHeaders = headers.getRequestHeaders();

        String userIp = requestHeaders.getFirst("X-Real-IP");

        log.info("Authenticating user ip: {}", userIp);

        UserModel user = context.getUser();
        if (user == null) {
            log.error("User is null!");
            context.failure(AuthenticationFlowError.INVALID_USER);
            return;
        }

        userLoginProtocolComponent.saveEnteredUser(user, userIp);

        String savedIp = user.getFirstAttribute("userIp");
        log.info("User last saved ip: {}", savedIp);

        if (savedIp != null && !savedIp.equals(userIp)) {
            revokeUserTokens(context.getSession(), context.getRealm(), user);

            user.setSingleAttribute("userIp", userIp);
        } else if (savedIp == null)
            user.setSingleAttribute("userIp", userIp);


        context.success();
        log.info("User authenticated with ip: {}", userIp);
    }

    private void revokeUserTokens(KeycloakSession session, RealmModel realm, UserModel user) {
        log.info("Revoking user tokens: {}", user.getFirstName());
        UserSessionProvider userSessionProvider = session.sessions();

        List<UserSessionModel> userSessions = userSessionProvider.getUserSessionsStream(realm, user)
                .toList();

        for (UserSessionModel userSession : userSessions) {
            userSessionProvider.removeUserSession(realm, userSession);
        }
    }


    @Override
    public void action(AuthenticationFlowContext context) {
        // Не требуется для простого аутентикатора
    }

    @Override
    public boolean requiresUser() {
        return true; // Требуется пользователь
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true; // Всегда готов к использованию
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // Не требуется
    }

    @Override
    public void close() {
        // Освобождение ресурсов
    }
}
