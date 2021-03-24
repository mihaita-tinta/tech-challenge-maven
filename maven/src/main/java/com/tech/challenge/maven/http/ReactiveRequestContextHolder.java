package com.tech.challenge.maven.http;

import org.springframework.http.server.ServerHttpRequest;
import reactor.core.publisher.Mono;

/**
 *
 * @Configuration
 * @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
 * public class ReactiveRequestContextFilter implements WebFilter {
 *
 *     @Override
 *     public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
 *         ServerHttpRequest request = exchange.getRequest();
 *         return chain.filter(exchange)
 *         .subscriberContext(ctx -> ctx.put(ReactiveRequestContextHolder.CONTEXT_KEY, request));
 *     }
 * }
 */
public class ReactiveRequestContextHolder {
    static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

    /**
     * Gets the {@code Mono<ServerHttpRequest>} from Reactor {@link reactor.util.context.Context}
     * @return the {@code Mono<ServerHttpRequest>}
     */
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.deferContextual(Mono::just)
                .map(ctx -> ctx.get(CONTEXT_KEY));
    }
}
