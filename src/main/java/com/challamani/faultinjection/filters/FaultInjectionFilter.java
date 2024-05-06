package com.challamani.faultinjection.filters;

import com.challamani.faultinjection.service.FaultInjectionService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@ConditionalOnProperty(name = "service.fault-injection", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class FaultInjectionFilter implements Filter {

    private final FaultInjectionService faultInjectionService;

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) servletRequest);

        String method = httpServletRequest.getMethod();
        Map<String, String> header = extractHeaders(httpServletRequest);

        Optional<HttpServletResponse> optionalHttpServletResponse = faultInjectionService
                .apply(method, header, (HttpServletResponse) servletResponse);

        if (optionalHttpServletResponse.isEmpty()) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headersMap = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        if (Objects.nonNull(headerNames)) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headersMap.put(headerName, headerValue);
            }
        }
        return Collections.unmodifiableMap(headersMap);
    }

}
