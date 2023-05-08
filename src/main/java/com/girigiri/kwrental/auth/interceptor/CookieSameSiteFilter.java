package com.girigiri.kwrental.auth.interceptor;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

public class CookieSameSiteFilter implements Filter {
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        chain.doFilter(request, response);
        final String setCookie = httpResponse.getHeader(HttpHeaders.SET_COOKIE);
        if (setCookie != null) httpResponse.setHeader(HttpHeaders.SET_COOKIE, setCookie + "; SameSite=");
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
