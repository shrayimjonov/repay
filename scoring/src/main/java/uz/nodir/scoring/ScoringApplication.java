package uz.nodir.scoring;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages =
        {
                "uz.nodir.scoring",
                "uz.repay.common",
                "uz.nodir.repayautoconfigure.*"
        }
)
@EnableFeignClients
@EnableEncryptableProperties
public class ScoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScoringApplication.class, args);
    }

}
