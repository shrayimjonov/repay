package uz.nodir.notification;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "uz.nodir.notification.*",
                "uz.repay.common.*",
                "uz.nodir.repayautoconfigure.*"
        }
)
@EntityScan(basePackages = {
        "uz.nodir.notification",
        "uz.repay.common"
})
@EnableEncryptableProperties
@EnableFeignClients
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }

}
