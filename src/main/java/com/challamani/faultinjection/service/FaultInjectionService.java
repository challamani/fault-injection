package com.challamani.faultinjection.service;

import com.challamani.faultinjection.config.FaultInjection;
import com.challamani.faultinjection.config.RequestHeader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FaultInjectionService {

    private final List<FaultInjection> faultInjections;

    @Autowired
    private FaultInjectionService(ResourceLoader resourceLoader) throws IOException {
        byte[] bytes = resourceLoader.getResource("classpath:fault-injections.json")
                .getInputStream().readAllBytes();
        String jsonData = new String(bytes).toString();
        log.info("faultInjection json data {}", jsonData);
        faultInjections = new ObjectMapper().readValue(jsonData, new TypeReference<>() {
        });
    }

    private Optional<HttpServletResponse> applyFixedDelay(HttpServletResponse httpServletResponse,
                                                          Integer milliSeconds) {
        try {
            log.info("<<< Requested is matched for fixed delay fault-injection, value {} milliSeconds", milliSeconds);
            Thread.sleep(milliSeconds);
            httpServletResponse.setHeader("fixedDelayFaultInjection","true");
            return Optional.empty();
        } catch (InterruptedException e) {
            log.error("failed at FaultInjectionService.applyFixedDelay  {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Optional<HttpServletResponse> applyAbort(HttpServletResponse httpServletResponse,
                                                 Integer httpStatus) {

        log.info("<<< Requested is matched for abort fault-injection, responding with {} httpStatus", httpStatus);
        httpServletResponse.setStatus(httpStatus);
        httpServletResponse.setHeader("abortFaultInjection", "true");
        return Optional.of(httpServletResponse);
    }

    public Optional<HttpServletResponse> apply(String method,
                                           Map<String, String> inboundHeaders,
                                           HttpServletResponse servletResponse) {

        Collections.sort(faultInjections, Comparator.comparing(FaultInjection::getPriority));
        log.debug("<<< inbound headers {} ",inboundHeaders);

        List<FaultInjection> applicableFaultInjections = faultInjections.stream()
                .filter(faultInjection -> faultInjection.getMatches().stream()
                        .anyMatch(matchRules ->
                                matchRules.getMethods().contains(method) &&
                                        inboundHeadersMatch(matchRules.getHeaders(), inboundHeaders)))
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(applicableFaultInjections)) {
            Optional<HttpServletResponse> optionalServletResponse = null;

            FaultInjection faultInjection = applicableFaultInjections.stream()
                    .filter(fi -> fi.getType()
                            .equalsIgnoreCase("delay"))
                    .findFirst().orElse(null);

            if (Objects.nonNull(faultInjection)) {
                String fixedDelay = inboundHeaders.getOrDefault("overridefixeddelay", Integer.toString(faultInjection.getFixedDelay()));
                optionalServletResponse = applyFixedDelay(servletResponse, Integer.valueOf(fixedDelay));
            }
            faultInjection = applicableFaultInjections.stream()
                    .filter(fi -> fi.getType()
                            .equalsIgnoreCase("abort"))
                    .findFirst().orElse(null);

            if (Objects.nonNull(faultInjection)) {
                String abortStatus = inboundHeaders.getOrDefault("overrideabort", Integer.toString(faultInjection.getHttpStatus()));
                optionalServletResponse = applyAbort(servletResponse, Integer.valueOf(abortStatus));
            }
            return optionalServletResponse;
        }
        return Optional.empty();
    }

    private boolean inboundHeadersMatch(List<RequestHeader> faultInjectionHeaders,
                                        Map<String, String> inboundHeaders) {

        return faultInjectionHeaders.stream()
                .allMatch(requestHeader ->
                        inboundHeaders.getOrDefault(requestHeader.getName(), "EMPTY")
                                .equalsIgnoreCase(requestHeader.getValue()));
    }
}
