package uz.repay.common.model.dto.response.keycloak;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRoleDTO(
        @JsonProperty("sub")
        String sub,
        @JsonProperty("email_verified")
        Boolean emailVerified,
        @JsonProperty("realm_access")
        RealmAccess realmAccess,
        @JsonProperty("client_branch")
        ArrayList<String> clientBranch,
        @JsonProperty("name")
        String name,
        @JsonProperty("preferred_username")
        String preferredUsername,
        @JsonProperty("given_name")
        String givenName,
        @JsonProperty("family_name")
        String family_name,
        @JsonProperty("email")
        String email
) {

    public UserRoleDTO(Map<String, Object> jwt) {
        this(
                (String) jwt.getOrDefault("sub", null),
                (Boolean) jwt.getOrDefault("email_verified", null),
                new RealmAccess((Map<String, Object>) jwt.getOrDefault("realm_access", new HashMap<String, Object>())),
                (ArrayList<String>) jwt.getOrDefault("client_branch", new ArrayList<String>()),
                (String) jwt.getOrDefault("name", null),
                (String) jwt.getOrDefault("preferred_username", null),
                (String) jwt.getOrDefault("given_name", null),
                (String) jwt.getOrDefault("family_name", null),
                (String) jwt.getOrDefault("email", null)
        );

    }

    public record RealmAccess(ArrayList<String> roles) {

        public RealmAccess(Map<String, Object> treeMap) {
            this(
                    (ArrayList<String>) treeMap.getOrDefault("roles", new ArrayList<>())
            );
        }
    }
}