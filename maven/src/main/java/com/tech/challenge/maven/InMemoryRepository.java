package com.tech.challenge.maven;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository {
    private final Map<String, WebSocketHolder> connections = new ConcurrentHashMap<>();

    public void onConnect(FluxSink<String> sink, String request) {
        connections.put(request, WebSocketHolder.of(sink, request));
    }
    public void onClose(String request) {
        connections.remove(request);
    }

    public void notify(String message) {

        connections.forEach((req, holder) -> {
            // TODO maybe filtering?
            sendInternal(holder, message);
        });
    }

    private void sendInternal(WebSocketHolder holder, String message) {
        FluxSink<String> sink = holder.sink;
        sink.next(message);
    }

    private static class WebSocketHolder {
        FluxSink<String> sink;
        String request;

        static WebSocketHolder of(FluxSink<String> sink, String request) {
            WebSocketHolder holder = new WebSocketHolder();
            holder.sink = sink;
            holder.request = request;
            return holder;
        }
    }
}
