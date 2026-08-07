package onon1101.lendingsystem.configurations.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

public class RequestContextFilter extends OncePerRequestFilter {

    // TODO Configure trusted forwarded headers when deploying behind a reverse proxy.
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = resolveRequestId(request);
        String clientIp = request.getRemoteAddr();
        String previousRequestId = MDC.get("requestId");
        String previousClientIp = MDC.get("clientIp");

        try {
            MDC.put("requestId", requestId);
            MDC.put("clientIp", clientIp);

            response.setHeader(REQUEST_ID_HEADER, requestId);
            filterChain.doFilter(request, response);
        } finally {
            restoreMdcValue("requestId", previousRequestId);
            restoreMdcValue("clientIp", previousClientIp);
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String suppliedId = request.getHeader(REQUEST_ID_HEADER);

        if (suppliedId != null && suppliedId.matches("[A-Za-z0-9_-]{8,64}")) {
            return suppliedId;
        }

        return UUID.randomUUID().toString();
    }

    private void restoreMdcValue(String key, String previousValue) {
        if (previousValue == null) {
            MDC.remove(key);
            return;
        }

        MDC.put(key, previousValue);
    }
}
