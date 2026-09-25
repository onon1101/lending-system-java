package onon1101.lendingsystem.configurations.controller;

import java.util.UUID;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class RequestContextFilter implements WebFilter {

    // TODO Configure trusted forwarded headers when deploying behind a reverse proxy.
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = resolveRequestId(request);
        String clientIp =
                request.getRemoteAddress() == null
                        ? "unknown"
                        : request.getRemoteAddress().getAddress().getHostAddress();

        exchange.getResponse().getHeaders().set(REQUEST_ID_HEADER, requestId);

        return chain.filter(exchange)
                .contextWrite(
                        context -> context.put("requestId", requestId).put("clientIp", clientIp));
    }

    private String resolveRequestId(ServerHttpRequest request) {
        String suppliedId = request.getHeaders().getFirst(REQUEST_ID_HEADER);

        if (suppliedId != null && suppliedId.matches("[A-Za-z0-9_-]{8,64}")) {
            return suppliedId;
        }

        return UUID.randomUUID().toString();
    }
}
