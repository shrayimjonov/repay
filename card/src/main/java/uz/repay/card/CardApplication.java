package uz.repay.card;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "uz.repay.card",
                "uz.repay.common",
                "uz.nodir.*"
        }
)
@EntityScan(basePackages = {
        "uz.repay.card",
        "uz.repay.common"
})
@EnableFeignClients
@EnableEncryptableProperties
public class CardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardApplication.class, args);
    }

}
