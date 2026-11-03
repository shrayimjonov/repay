package uz.nodir.chat_service;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "uz.nodir.chat_service",
                "uz.repay.common",
                "uz.nodir.repayautoconfigure.*"
        }
)
@EnableDiscoveryClient
@EnableFeignClients
@EnableEncryptableProperties
public class ChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }

}
