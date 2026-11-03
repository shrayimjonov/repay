package uz.repay.apigate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        var services = List.of("payment", "card", "autodebit", "loan", "report", "sms", "file", "notification", "scoring");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        services
                .forEach(z -> simpleRestRequestSource(source, z));


        return new CorsWebFilter(source);
    }


    private static void simpleRestRequestSource(UrlBasedCorsConfigurationSource source, String path) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOriginPattern("*"); // Allow all origins (you can restrict this)
        corsConfig.addAllowedMethod("*"); // Allow all HTTP methods
        corsConfig.addAllowedHeader("*"); // Allow all headers
        corsConfig.setAllowCredentials(true); // Allow credentials (e.g., cookies, authentication headers)


        source.registerCorsConfiguration("/" + path + "/**", corsConfig);
    }
}
