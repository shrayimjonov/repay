package uz.repay.card.client.configuration;

import feign.Client;
import feign.Logger;
import feign.Request;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import uz.repay.card.model.property.MasterProperty;
import uz.repay.common.exception.GeneralErrorException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
public class MasterGatewayClientConfiguration {
    private final MasterProperty property;

    @Bean
    public Logger.Level masterGateFeignLoggerLevel() {
        var logger = Logger.Level.FULL;
        log.info("Prepared masterGate logger: {}", logger);
        return logger;
    }

    @Bean
    public ErrorDecoder masterGateErrorDecoder() {
        var errorDecoder = new ErrorDecoder.Default();
        log.info("Built masterGate error decoder: {}", errorDecoder);
        return errorDecoder;
    }


    @Bean
    public Request.Options masterGateRequestTimeOuts() {
        var ro = new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
        log.info("Request options for masterGate: {}", ro);
        return ro;
    }

    @Bean
    public Client masterGatefeignClient() {
        var client = new Client.Default(masterGateGetSSLSocketFactory(), new NoopHostnameVerifier());
        log.info("Chosen masterGate client with SSL settings: {}", client);
        return client;
    }

    private SSLSocketFactory masterGateGetSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
            return sslContext.getSocketFactory();
        } catch (Exception ex) {
            throw new GeneralErrorException(ex.getMessage());
        }
    }

    @Bean
    public BasicAuthRequestInterceptor masterGateBasicAuthRequestInterceptor() {
        log.info("Basic user taken to masterGate request: {}", property.getUsername());
        return new BasicAuthRequestInterceptor(property.getUsername(), property.getPassword());
    }
}
