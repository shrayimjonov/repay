package uz.repay.report;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import uz.nodir.repayautoconfigure.configuration.database.PersistenceConfig;

@SpringBootApplication(
        scanBasePackages = {
                "uz.repay.report",
                "uz.repay.common",
                "uz.nodir.repayautoconfigure"
        }
)
@ComponentScan(excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = PersistenceConfig.class))
@EnableFeignClients
@EnableEncryptableProperties
public class ReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }

}
