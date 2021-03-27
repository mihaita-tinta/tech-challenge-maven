package com.tech.challenge.maven.http;

import com.tech.challenge.maven.config.MavenConfigurationProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.CountDownLatch;

@Configuration
public class WebClientConfig {

//    @LoadBalanced
    @Bean
    @Primary
    WebClient webClient(MavenConfigurationProperties props) {

        TcpClient tcpClient = TcpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(10))
                        .addHandlerLast(new WriteTimeoutHandler(10)));

       return WebClient.builder()
                .baseUrl(props.getHttp().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
                .build();
    }

    private ExchangeFilterFunction headerFilter() {
        return (request, next) -> ReactiveRequestContextHolder.getRequest()
                .flatMap(r -> {
                    ClientRequest clientRequest = ClientRequest.from(request)
                            .headers(headers -> {
                                headers.set(HttpHeaders.ORIGIN, r.getHeaders().getFirst(HttpHeaders.ORIGIN));
                                headers.set(HttpHeaders.AUTHORIZATION, r.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
                            })
                            .build();
                    return next.exchange(clientRequest);
                });
    }

}
