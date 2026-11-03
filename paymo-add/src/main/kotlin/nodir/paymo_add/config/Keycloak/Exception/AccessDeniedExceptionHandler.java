package nodir.paymo_add.config.Keycloak.Exception;


import net.minidev.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        response.setContentType("application/json");
        response.setStatus(423);
        JSONObject json = new JSONObject();
        json.put("code", "423");
        json.put("detail", "Invalid grant");
        json.put("message", "You haven't access to this method");

        response.getWriter().print(json.toJSONString());
        response.getWriter().flush();

    }
}
