package uz.nodir.repayotp.config;

import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.utils.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.nodir.repayotp.service.MailSenderComponent;
import uz.nodir.repayotp.service.StorageComponent;
import uz.nodir.repayotp.service.UserTrustComponent;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static uz.nodir.repayotp.model.enums.ResultTypes.*;

public class RepayEmailOtpAuthenticator implements Authenticator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final MailSenderComponent mailSenderComponent = new MailSenderComponent();

    private final StorageComponent storageComponent = new StorageComponent();
    private final UserTrustComponent userTrustComponent = new UserTrustComponent();


    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        String username = user.getUsername();

        log.info("Starting authenticating with email: {}", username);

        String sessionId = context.getHttpRequest().getDecodedFormParameters().getFirst(SESSION_ID.name());

        var ipAddress = takeUserIPAddress(context);
        log.info("Received IP address: {}", ipAddress);

        var trustedResult = userTrustComponent.findTrustedUser(username, ipAddress);

        if (trustedResult.isPresent()) {
            log.info("Method finished. Trusted user: {}", trustedResult.get());
            context.success();
            return;
        }


        if (sessionId != null) {
            log.info("SessionID is present: {}", sessionId);
            var otpResult = storageComponent.getOtp(sessionId);

            if (otpResult.isPresent()) {
                log.info("Session ID: {} is found from verification storage", sessionId);
                action(context);
                return;
            }

            log.info("Session ID: {} is not found and will be generated again", sessionId);
        }

        // Генерация 6-значного кода
        String otpCode = String.valueOf(new Random().nextInt(100000, 1000000));


        mailSenderComponent.sendEmail(username, otpCode);

        var generatedSessionId = UUID.randomUUID().toString();

        storageComponent.deleteAllSessionsByUsername(username);
        storageComponent.saveOtp(username, generatedSessionId, otpCode);


        // Возвращаем JSON-ответ, что требуется OTP-код
        var challengeResponse = Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(
                        Map.of(
                                "error", OTP_REQUIRED,
                                "session_id", generatedSessionId
                        )
                )
                .type(MediaType.APPLICATION_JSON)
                .build();

        context.failureChallenge(AuthenticationFlowError.CREDENTIAL_SETUP_REQUIRED, challengeResponse);
    }


    @Override
    public void action(AuthenticationFlowContext context) {
        String enteredCode = context.getHttpRequest().getDecodedFormParameters().getFirst(OTP_CODE.name());
        String sessionId = context.getHttpRequest().getDecodedFormParameters().getFirst(SESSION_ID.name());

        var expectedCodeResult = storageComponent.getOtp(sessionId);

        log.info("Received otp code: {}", enteredCode);

        if (expectedCodeResult.isEmpty()) {

            log.error("SessionID: {} not found in storage", sessionId);

            Response errorResponse = Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", SESSION_EXPIRATION))
                    .type(MediaType.APPLICATION_JSON)
                    .build();

            context.failureChallenge(AuthenticationFlowError.INVALID_CLIENT_SESSION, errorResponse);

        } else if (enteredCode != null && enteredCode.equals(expectedCodeResult.get())) {
            log.info("OTP verified successfully for user: {}", context.getUser().getEmail());
            storageComponent.deleteOtpBySessionId(sessionId);

            var ipAddress = takeUserIPAddress(context);

            userTrustComponent.save(context.getUser().getUsername(), ipAddress);
            context.success();
        } else {
            log.error("Invalid OTP for user: {}", context.getUser().getEmail());

            Response errorResponse = Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", INVALID_OTP))
                    .type(MediaType.APPLICATION_JSON)
                    .build();

            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, errorResponse);
        }
    }

    private static String takeUserIPAddress(AuthenticationFlowContext context) {
        return context.getHttpRequest().getHttpHeaders().getRequestHeaders().getFirst("X-Real-IP");
   //return context.getConnection().getRemoteAddr();
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {
    }
}