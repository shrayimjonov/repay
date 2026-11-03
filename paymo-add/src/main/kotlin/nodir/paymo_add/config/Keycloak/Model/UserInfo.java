package nodir.paymo_add.config.Keycloak.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class UserInfo {
    @JsonProperty("sub")
    @ApiModelProperty(value = "User Id from Keycloak")
    private String useId;
    @JsonProperty("email_verified")
    @ApiModelProperty(value = "Email is confirmed or not")
    private boolean emailVerified;
    @JsonProperty("preferred_username")
    @ApiModelProperty(value = "Username from Keycloak")
    private String userName;
    @JsonProperty("name")
    @ApiModelProperty(value = "User Full name from Keycloak")
    private String name;
    @JsonProperty("user_roles")
    @ApiModelProperty(value = "User assigned roles")
    private ArrayList<String> userRoles;
    private String error;
    @JsonProperty("client_branch")
    @ApiModelProperty(value = "User assigned branches")
    private ArrayList<String> client_branch;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}


