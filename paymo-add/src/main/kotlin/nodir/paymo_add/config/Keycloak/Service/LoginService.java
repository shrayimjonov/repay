package nodir.paymo_add.config.Keycloak.Service;


import nodir.paymo_add.config.Keycloak.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;


@Service
public class LoginService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issueUrl;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.keycloak.authorization-grant-type}")
    private String grantType;


    public ResponseEntity<LoginResponse> login(LoginRequest loginrequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", grantType);
        map.add("username", loginrequest.getUsername());
        map.add("password", loginrequest.getPassword());
        map.add("scope", "openid");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(issueUrl + "/protocol/openid-connect/token", httpEntity, LoginResponse.class);
            return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
        } catch (Exception ex) {
            LoginResponse response = new LoginResponse();
            response.setError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


    }


    public ResponseEntity<Response> logout(TokenRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", request.getToken());
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<Response> response = restTemplate.postForEntity(issueUrl + "/protocol/openid-connect/logout", httpEntity, Response.class);
        Response res = new Response();
        if (response.getStatusCode().is2xxSuccessful()) {
            res.setMessage("Logged out successfully");
        }
        try {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception ex) {
            Response resp = new Response();
            resp.setError(ex.getMessage());
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<IntrospectResponse> introspect(TokenRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("token", request.getToken());
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<IntrospectResponse> response = restTemplate.postForEntity(issueUrl + "/protocol/openid-connect/token/introspect", httpEntity, IntrospectResponse.class);
            return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
        } catch (Exception ex) {
            IntrospectResponse introspectResponse = new IntrospectResponse();
            introspectResponse.setError(ex.getMessage());
            return new ResponseEntity<>(introspectResponse, HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<UserInfo> userInfo(TokenRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(request.getToken());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<UserInfo> response = restTemplate.postForEntity(issueUrl + "/protocol/openid-connect/userinfo", httpEntity, UserInfo.class);
            return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
        } catch (Exception ex) {
            UserInfo userInfo = new UserInfo();
            userInfo.setError(ex.getMessage());
            return new ResponseEntity<>(userInfo, HttpStatus.BAD_REQUEST);
        }
    }

    public TokenRequest findToken(ServletRequest servlet) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servlet;
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(httpServletRequest.getHeader("Authorization").substring(httpServletRequest.getHeader("Authorization").indexOf(" ") + 1));
        return tokenRequest;
    }



}
