package nodir.paymo_add.service;

import javax.servlet.ServletRequest;

public interface LogProtocolService {

    public void addLogProtocol(ServletRequest servletRequest);

    public String findUsername(ServletRequest servletRequest);
}
