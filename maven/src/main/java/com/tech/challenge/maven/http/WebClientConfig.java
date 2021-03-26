package com.tech.challenge.maven.http;

import com.tech.challenge.maven.config.MavenConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

//    @LoadBalanced
    @Bean
    @Primary
    WebClient webClient(MavenConfigurationProperties props) {
        return WebClient.builder()
                .baseUrl(props.getHttp().getBaseUrl())
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
