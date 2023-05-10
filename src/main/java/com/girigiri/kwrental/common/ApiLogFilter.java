package com.girigiri.kwrental.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ApiLogFilter extends OncePerRequestFilter {

    private static final String START_OF_PARAMS = "?";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        final ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
        final ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        final StopWatch requestTimeStopWatch = new StopWatch();
        requestTimeStopWatch.start();
        filterChain.doFilter(cachingRequest, cachingResponse);
        requestTimeStopWatch.stop();

        logRequestAndResponse(cachingRequest, cachingResponse, requestTimeStopWatch);
        cachingResponse.copyBodyToResponse();
    }

    private void logRequestAndResponse(final ContentCachingRequestWrapper request,
                                       final ContentCachingResponseWrapper response, final StopWatch requestTimeStopWatch) {
        logConsumedTime(request, response, requestTimeStopWatch);
        logBody(request, response);
    }

    private void logBody(final ContentCachingRequestWrapper request, final ContentCachingResponseWrapper response) {
        final String requestBody = new String(request.getContentAsByteArray());
        final HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        if (httpStatus.isError() && !requestBody.isBlank()) {
            log.warn("[Failed Request Body] : {}", requestBody);
            getJsonResponseBody(response)
                    .ifPresent(body -> log.warn("[Response Body] : {}", body));
            log.warn("[Response Body] : {}", response);
        }
        if (httpStatus.is2xxSuccessful() && !requestBody.isBlank()) {
            log.info("[Request Body] : {}", requestBody);
            getJsonResponseBody(response)
                    .ifPresent(body -> log.info("[Response Body] : {}", body));
        }
    }

    private Optional<String> getJsonResponseBody(final ContentCachingResponseWrapper response) {
        if (Objects.equals(response.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            return Optional.of(new String(response.getContentAsByteArray()));
        }
        return Optional.empty();
    }

    private void logConsumedTime(final HttpServletRequest request, final HttpServletResponse response, final StopWatch requestTimeStopWatch) {
        final long totalTimeMillis = requestTimeStopWatch.getTotalTimeMillis();
        final double seconds = totalTimeMillis / 1000.0;
        final HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        final String requestURI = getRequestURIWithParams(request);
        if (seconds >= 0.5 || httpStatus.isError()) {
            log.warn("[{}] {}:{} {} seconds", httpStatus, request.getMethod(), requestURI, seconds);
        } else {
            log.info("[{}] {}:{} {} seconds", httpStatus, request.getMethod(), requestURI, seconds);
        }
    }

    private String getRequestURIWithParams(final HttpServletRequest request) {
        final String requestURI = request.getRequestURI();
        final Map<String, String[]> params = request.getParameterMap();
        if (params.isEmpty()) {
            return requestURI;
        }
        final String parsedParams = parseParams(params);
        return requestURI + parsedParams;
    }

    private String parseParams(final Map<String, String[]> params) {
        final String everyParamStrings = params.entrySet().stream()
                .map(this::toParamString)
                .collect(Collectors.joining(PARAM_DELIMITER));
        return START_OF_PARAMS + everyParamStrings;
    }

    private String toParamString(final Map.Entry<String, String[]> entry) {
        final String key = entry.getKey();
        final StringBuilder builder = new StringBuilder();
        return Arrays.stream(entry.getValue())
                .map(value -> builder.append(key).append(KEY_VALUE_DELIMITER).append(value))
                .collect(Collectors.joining(PARAM_DELIMITER));
    }
}
