package nodir.paymo_add.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestService {
    @Value("${nci.gateway}")
    private String server7065;

    private final RestTemplate restTemplate;

    public Object bankDay() {
        ResponseEntity<Object> response = restTemplate.getForEntity(server7065 + "/day/v2", Object.class);
        return response.getBody();
    }
}
