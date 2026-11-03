package nodir.paymo_add.service;

import lombok.RequiredArgsConstructor;
import nodir.paymo_add.repository.LogProtocolRepo;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;

@Service
@RequiredArgsConstructor
public class LogProtocolServiceImpl implements LogProtocolService{
    private final LogProtocolRepo logProtocolRepo;


    @Override
    public void addLogProtocol(ServletRequest servletRequest) {

    }

    @Override
    public String findUsername(ServletRequest servletRequest) {
        return null;
    }
}
