package uz.repay.autodebit.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.function.Function;


@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // Таймаут подключения
                        .responseTimeout(Duration.ofSeconds(10)) // Таймаут ответа
                        .doOnConnected(conn -> conn
                                .addHandlerLast(new ReadTimeoutHandler(10)) // Чтение
                                .addHandlerLast(new WriteTimeoutHandler(10)) // Запись
                        )
                        .compress(true) // Сжатие gzip
                        .wiretap("reactor.netty.http.client", LogLevel.DEBUG)
                        .metrics(true, Function.identity())
                ))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB буфер
                        .build())
                ;
    }
}
