package uz.nodir.repayautoconfigure.utils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import uz.nodir.repayautoconfigure.service.JwtAuthConverter;
import uz.repay.common.exception.BranchException;
import uz.repay.common.exception.PermissionException;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.keycloak.UserRoleDTO;
import uz.repay.common.model.enums.ResponseStatus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtils {

    private final JwtAuthConverter jwtAuthConverter;
    public static final String principalAttribute = "preferred_username";


    public String checkBranchPermission(String branch, SecurityContext securityContext) {
        var branches = extractBranches(securityContext);

        var authentication = securityContext.getAuthentication();

        if (branches
                .stream()
                .noneMatch(z -> z.equals(branch)))
            throw new PermissionException("You dont have permission for branch: %s".formatted(branch));

        return (String) ((JwtAuthenticationToken) authentication).getToken().getClaims().get(principalAttribute);
    }


    public static String getUsername() {
        var securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication().getPrincipal().toString().equals("anonymousUser"))
            return "system";
        return (String) ((JwtAuthenticationToken) securityContext.getAuthentication()).getToken().getClaims().get(principalAttribute);
    }


    public static String getUsernameV2() {
        var securityContext = SecurityContextHolder.getContext();

        if (securityContext == null || securityContext.getAuthentication() == null)
            return "system";
        else if (securityContext.getAuthentication() instanceof AnonymousAuthenticationToken || ((JwtAuthenticationToken) securityContext.getAuthentication()).getToken() == null)
            return "external";

        return (String) ((JwtAuthenticationToken) securityContext.getAuthentication()).getToken().getClaims().get(principalAttribute);
    }

    @SuppressWarnings("unchecked")
    private static List<String> getUserBranches(SecurityContext securityContext) {
        return (List<String>) ((JwtAuthenticationToken) securityContext.getAuthentication()).getToken().getClaims().get("client_branch");
    }

    public static List<String> getUserBranches() {
        var branchList = getUserBranches(SecurityContextHolder.getContext());

        if (branchList == null || branchList.isEmpty())
            throw new BranchException("User doesn't have branches in role!");

        return branchList;
    }

    public static void checkUserRequestedBranch(String loanId) {
        var branchList = getUserBranches();
        var branch = loanId.substring(0, 5);

        if (branchList
                .stream()
                .noneMatch(z -> z.equals(branch)))
            throw new BranchException("User doesn't have permission to branch: %s!".formatted(branch));
    }


    private List<String> extractBranches(SecurityContext securityContext) {
        return jwtAuthConverter.extractBranches((Jwt) securityContext.getAuthentication().getPrincipal())
                .stream()
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

    public static List<String> extractRoles() {
        var jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = new UserRoleDTO(jwt.getClaims());
        return user.realmAccess().roles()
                .stream()
                .map(Objects::toString)
                .toList();
    }

    public static SecurityContext securityContext() {
        return SecurityContextHolder.getContext();
    }

    public static SimpleResponse<CoreError> checkUserToReal(String username) {
        if (username.equals("system") || username.equals("external")) {
            var message = "User: %s is not real and will be exited from method".formatted(username);
            log.error(message);
            return new SimpleResponse<>(-12, message);
        }
        return new SimpleResponse<>(new CoreError(0, ResponseStatus.SUCCESS.name()));
    }
}