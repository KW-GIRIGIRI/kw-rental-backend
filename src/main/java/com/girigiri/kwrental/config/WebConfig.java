package com.girigiri.kwrental.config;

import com.girigiri.kwrental.common.CustomHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final List<CustomHandlerMethodArgumentResolver> argumentResolvers;

    public WebConfig(final List<CustomHandlerMethodArgumentResolver> argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods(GET.name(), OPTIONS.name(), POST.name(), DELETE.name(),
                        PUT.name(), HEAD.name(), PATCH.name(), TRACE.name())
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.LOCATION, HttpHeaders.AUTHORIZATION, HttpHeaders.SET_COOKIE)
                .allowedOrigins("http://localhost:3000");
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(argumentResolvers);
    }
}
