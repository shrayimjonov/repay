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
import uz.repay.card.model.property.SVGateProperty;
import uz.repay.common.exception.GeneralErrorException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class SVGateClientConfiguration {
    private final SVGateProperty property;

    @Bean
    public Logger.Level svGateFeignLoggerLevel() {
        var logger = Logger.Level.FULL;
        log.info("Prepared svGate logger: {}", logger);
        return logger;
    }

    @Bean
    public ErrorDecoder svGateErrorDecoder() {
        var errorDecoder = new ErrorDecoder.Default();
        log.info("Built svGate error decoder: {}", errorDecoder);
        return errorDecoder;
    }


    @Bean
    public Request.Options svGateRequestTimeOuts() {
        var ro = new Request.Options(
                property.getConnectTimeOut(), TimeUnit.MILLISECONDS,
                property.getReadTimeOut(), TimeUnit.MILLISECONDS,
                true);
        log.info("Request options for svGate: {}", ro);
        return ro;
    }

    @Bean
    public Client svGatefeignClient() {
        var client = new Client.Default(svGateGetSSLSocketFactory(), new NoopHostnameVerifier());
        log.info("Chosen svGate client with SSL settings: {}", client);
        return client;
    }

    private SSLSocketFactory svGateGetSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
            return sslContext.getSocketFactory();
        } catch (Exception ex) {
            throw new GeneralErrorException(ex.getMessage());
        }
    }

    @Bean
    public BasicAuthRequestInterceptor svGateBasicAuthRequestInterceptorSVGate() {
        log.info("Basic user taken to svGate request: {}", property.getUsername());
        return new BasicAuthRequestInterceptor(property.getUsername(), property.getPassword());
    }
}
