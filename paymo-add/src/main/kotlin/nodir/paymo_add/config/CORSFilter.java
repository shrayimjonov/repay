//package nodir.paymo_add.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//import tech.jhipster.config.JHipsterProperties;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class CORSFilter implements Filter {
//
//
//    private final JHipsterProperties jHipsterProperties;
//
//    public CORSFilter(JHipsterProperties jHipsterProperties) {
//        this.jHipsterProperties = jHipsterProperties;
//    }
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) res;
//        HttpServletRequest request = (HttpServletRequest) req;
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "*");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "*");
//     //   response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//   //     response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//   //     response.setHeader("Access-Control-Max-Age", "3600");
//    //    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
//
//        //response.setHeader("Access-Control-Expose-Headers","yourCustomHeaderIfExist");
//
////        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
////            response.setStatus(HttpServletResponse.SC_OK);
////        }
////        else {
//            chain.doFilter(req, res);
//        //}
//    }
//
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = jHipsterProperties.getCors();
//        if (!CollectionUtils.isEmpty(config.getAllowedOrigins()) || !CollectionUtils.isEmpty(config.getAllowedOriginPatterns())) {
//            source.registerCorsConfiguration("/api/**", config);
//            source.registerCorsConfiguration("/management/**", config);
//            source.registerCorsConfiguration("/v2/api-docs", config);
//            source.registerCorsConfiguration("/v3/api-docs", config);
//            source.registerCorsConfiguration("/swagger-resources", config);
//            source.registerCorsConfiguration("/swagger-ui/**", config);
//        }
//        return new CorsFilter(source);
//    }
//
//    @Override
//    public void init(FilterConfig filterConfig) {
//    }
//
//    @Override
//    public void destroy() {
//    }
//
//}