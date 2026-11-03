package uz.davrbank.repay.commonlogging.config;

import feign.RequestInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uz.davrbank.repay.commonlogging.feign.TracePropagationInterceptor;
import uz.davrbank.repay.commonlogging.publisher.KafkaLogEventPublisher;
import uz.davrbank.repay.commonlogging.publisher.LogEventPublisher;
import uz.davrbank.repay.commonlogging.publisher.NoOpLogEventPublisher;
import uz.davrbank.repay.commonlogging.security.UsernameJwtAuthenticationConverter;
import uz.davrbank.repay.commonlogging.support.StartupBanner;
import uz.davrbank.repay.commonlogging.web.ApiLogFilter;
import uz.davrbank.repay.commonlogging.web.TraceContextFilter;
import uz.davrbank.repay.commonlogging.web.UsernameCaptureInterceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AutoConfiguration
@EnableConfigurationProperties(LoggingProperties.class)
@ConditionalOnProperty(prefix = "repay.logging", name = "enabled", matchIfMissing = true)
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "apiLogKafkaTemplate")
    @ConditionalOnClass(KafkaTemplate.class)
    @ConditionalOnProperty(prefix = "spring.kafka", name = "bootstrap-servers")
    public KafkaTemplate<String, Object> apiLogKafkaTemplate(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        props.putAll(kafkaProperties.getProperties());
        KafkaProperties.Producer producer = kafkaProperties.getProducer();
        props.putAll(producer.getProperties());
        List<String> bootstrapServers = producer.getBootstrapServers() != null
                ? producer.getBootstrapServers()
                : kafkaProperties.getBootstrapServers();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        ProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(props);
        return new KafkaTemplate<>(factory);
    }

    @Bean
    @ConditionalOnMissingBean(LogEventPublisher.class)
    @ConditionalOnClass(KafkaTemplate.class)
    @ConditionalOnProperty(prefix = "spring.kafka", name = "bootstrap-servers")
    public LogEventPublisher kafkaLogEventPublisher(
            @Qualifier("apiLogKafkaTemplate") KafkaTemplate<String, Object> apiLogKafkaTemplate,
            ObjectProvider<MeterRegistry> meterRegistry,
            LoggingProperties props,
            Environment env) {
        resolveServiceName(props, env);
        return new KafkaLogEventPublisher(apiLogKafkaTemplate, meterRegistry.getIfAvailable());
    }


    @Bean
    @ConditionalOnMissingBean(LogEventPublisher.class)
    public LogEventPublisher noOpLogEventPublisher() {
        return new NoOpLogEventPublisher();
    }

    @Bean
    @ConditionalOnMissingBean
    public StartupBanner startupBanner(Environment environment) {
        return new StartupBanner(environment);
    }

    private static void resolveServiceName(LoggingProperties props, Environment env) {
        if (!StringUtils.hasText(props.getService())) {
            props.setService(env.getProperty("spring.application.name", "unknown-service"));
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class WebLoggingConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public TraceContextFilter traceContextFilter() {
            return new TraceContextFilter();
        }

        @Bean
        @ConditionalOnMissingBean
        public ApiLogFilter apiLogFilter(LogEventPublisher publisher,
                                         LoggingProperties props,
                                         Environment env) {
            resolveServiceName(props, env);
            return new ApiLogFilter(publisher, props);
        }

        @Bean
        @ConditionalOnMissingBean(name = "apiLogUsernameWebMvcConfigurer")
        public WebMvcConfigurer apiLogUsernameWebMvcConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(new UsernameCaptureInterceptor());
                }
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(RequestInterceptor.class)
    @ConditionalOnProperty(prefix = "repay.logging", name = "feign-enabled", matchIfMissing = true)
    static class FeignLoggingConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public TracePropagationInterceptor tracePropagationInterceptor() {
            return new TracePropagationInterceptor();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(JwtAuthenticationConverter.class)
    static class SecurityLoggingConfiguration {

        @Bean
        @ConditionalOnMissingBean(JwtAuthenticationConverter.class)
        public JwtAuthenticationConverter apiLogJwtAuthenticationConverter(LoggingProperties props) {
            return UsernameJwtAuthenticationConverter.forClaim(props.getUsernameClaim());
        }
    }
}
