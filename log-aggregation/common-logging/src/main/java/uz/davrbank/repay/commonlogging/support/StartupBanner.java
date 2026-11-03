package uz.davrbank.repay.commonlogging.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class StartupBanner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupBanner.class);
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy :> hh:mm:ss a");

    private final Environment env;

    public StartupBanner(Environment env) {
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        String app = env.getProperty("spring.application.name", "application");
        String startedAt = FORMAT.format(ZonedDateTime.now(ZoneId.of("Asia/Tashkent")));
        log.info("""
                        
                        
                        ____________________REPAY______________________________
                        
                        {} service STARTED SUCCESSFULLY
                        authors: ..............: Repay team (Shukhratjon Rayimjonov)
                        application: ..........: {}
                        active profiles: ......: {}
                        address: ..............: {}
                        started at: ...........: {}
                        _____________________________________________________""",
                app,
                app,
                Arrays.toString(env.getActiveProfiles()),
                address(),
                startedAt);
    }

    private String address() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            host = "localhost";
            log.warn("Can't determine host address");
        }
        return "http://" + host + ":" + env.getProperty("server.port", "8080");
    }
}