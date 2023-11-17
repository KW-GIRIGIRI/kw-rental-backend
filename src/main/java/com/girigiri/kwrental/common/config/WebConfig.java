package com.girigiri.kwrental.common.config;

import static org.springframework.http.HttpMethod.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.girigiri.kwrental.auth.service.KwangwoonMemberService;
import com.girigiri.kwrental.common.log.ApiLogFilter;
import com.girigiri.kwrental.common.CustomHandlerMethodArgumentResolver;
import com.girigiri.kwrental.common.exception.KwangwoonServerException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final List<CustomHandlerMethodArgumentResolver> argumentResolvers;

    @Value("${cors.allow-origins}")
    private final String[] allowOrigins;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedMethods(GET.name(), OPTIONS.name(), POST.name(), DELETE.name(),
                PUT.name(), HEAD.name(), PATCH.name(), TRACE.name())
            .allowCredentials(true)
            .exposedHeaders(HttpHeaders.LOCATION, HttpHeaders.AUTHORIZATION, HttpHeaders.SET_COOKIE)
            .allowedOrigins(allowOrigins);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(argumentResolvers);
    }

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> requestLoggingFilter() {
        final FilterRegistrationBean<OncePerRequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ApiLogFilter());
        filterRegistrationBean.addUrlPatterns("/api/*");
        return filterRegistrationBean;
    }

    @Bean
    public KwangwoonMemberService kwangwoonMemberService() {
        final WebClient kwangwoonServerClient = createKwangwoonServerClient();
        final HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builder(WebClientAdapter.forClient(kwangwoonServerClient))
            .build();
        return factory.createClient(KwangwoonMemberService.class);
    }

    private WebClient createKwangwoonServerClient() {
        return WebClient.builder()
            .baseUrl("https://klas.kw.ac.kr/")
            .defaultStatusHandler(HttpStatusCode::isError, res -> Mono.just(new KwangwoonServerException()))
            .build();
    }
}
