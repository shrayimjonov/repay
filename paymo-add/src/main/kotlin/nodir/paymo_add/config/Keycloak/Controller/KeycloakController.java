package nodir.paymo_add.config.Keycloak.Controller;


import nodir.paymo_add.config.Keycloak.Model.*;
import nodir.paymo_add.config.Keycloak.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class KeycloakController {


    @Autowired
    LoginService loginservice;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest loginrequest) {
        return loginservice.login(loginrequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout (@RequestBody TokenRequest token) {
        return loginservice.logout(token);
    }

    @PostMapping("/introspect")
    public ResponseEntity<IntrospectResponse> introspect(@RequestBody TokenRequest token) {
        return loginservice.introspect(token);
    }

    @PostMapping("/userInfo")
    public ResponseEntity<UserInfo> userInfo(@RequestBody TokenRequest token) {
        return loginservice.userInfo(token);
    }

}
