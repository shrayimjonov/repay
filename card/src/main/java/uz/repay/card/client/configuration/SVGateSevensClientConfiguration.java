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
import uz.repay.card.model.property.SVGateSevensProperty;
import uz.repay.common.exception.GeneralErrorException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class SVGateSevensClientConfiguration {
    private final SVGateSevensProperty property;

    @Bean
    public Logger.Level svGateSevensFeignLoggerLevel() {
        var logger = Logger.Level.FULL;
        log.info("Prepared svGate sevens logger: {}", logger);
        return logger;
    }

    @Bean
    public ErrorDecoder svGateSevensErrorDecoder() {
        var errorDecoder = new ErrorDecoder.Default();
        log.info("Built svGate sevens error decoder: {}", errorDecoder);
        return errorDecoder;
    }


    @Bean
    public Request.Options svGateSevensRequestTimeOuts() {
        var ro = new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
        log.info("Request options for svGate sevens: {}", ro);
        return ro;
    }

    @Bean
    public Client svGateSevensfeignClient() {
        var client = new Client.Default(svGateGetSevensSSLSocketFactory(), new NoopHostnameVerifier());
        log.info("Chosen svGate sevens client with SSL settings: {}", client);
        return client;
    }

    private SSLSocketFactory svGateGetSevensSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
            return sslContext.getSocketFactory();
        } catch (Exception ex) {
            throw new GeneralErrorException(ex.getMessage());
        }
    }

    @Bean
    public BasicAuthRequestInterceptor svGateSevensBasicAuthRequestInterceptorSVGate() {
        log.info("Basic user taken to svGate Sevens request: {}", property.getUsername());
        return new BasicAuthRequestInterceptor(property.getUsername(), property.getPassword());
    }
}
