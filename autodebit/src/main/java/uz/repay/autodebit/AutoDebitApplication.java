package uz.repay.autodebit;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "uz.repay.autodebit",
                "uz.repay.common",
                "uz.nodir.*"
        }
)
@EnableFeignClients
@EnableEncryptableProperties
public class AutoDebitApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoDebitApplication.class, args);
    }

}
