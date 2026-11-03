package nodir.paymo_add.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogProtocol {
    private Long id;
    private String userName;
    private LocalDateTime addedTime;
    private String requestURI;
    private String requestURL;
    private String remoteAddr;
    private String ServerPort;
}
