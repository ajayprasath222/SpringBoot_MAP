package com.example.printer_springbe.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Normalizes duplicate slashes in the request URI (e.g. {@code //api/v1/...} → {@code /api/v1/...})
 * so Spring MVC route matching works correctly.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PathNormalizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.contains("//")) {
            filterChain.doFilter(new NormalizedRequest(request, uri.replaceAll("/+", "/")), response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static final class NormalizedRequest extends HttpServletRequestWrapper {

        private final String normalizedUri;

        private NormalizedRequest(HttpServletRequest request, String normalizedUri) {
            super(request);
            this.normalizedUri = normalizedUri;
        }

        @Override
        public String getRequestURI() {
            return normalizedUri;
        }

        @Override
        public String getServletPath() {
            String servletPath = super.getServletPath();
            if (servletPath != null && servletPath.contains("//")) {
                return servletPath.replaceAll("/+", "/");
            }
            return servletPath;
        }
    }
}
