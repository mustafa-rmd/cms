package com.thmanyah.services.cms.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Global CORS filter to handle preflight requests and ensure CORS headers are set
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalCorsFilter implements Filter {

    @Value("${cms.cors.allowed-origins:http://localhost:4200,http://localhost:3000,http://127.0.0.1:4200,http://127.0.0.1:3000}")
    private String[] allowedOrigins;

    @Value("${cms.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cms.cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${cms.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cms.cors.max-age:3600}")
    private long maxAge;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        
        // Check if origin is allowed
        if (origin != null && isOriginAllowed(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else if (allowedOrigins.length == 1 && "*".equals(allowedOrigins[0])) {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }

        // Set CORS headers
        response.setHeader("Access-Control-Allow-Methods", String.join(",", allowedMethods));
        
        if (allowedHeaders.length == 1 && "*".equals(allowedHeaders[0])) {
            response.setHeader("Access-Control-Allow-Headers", "*");
        } else {
            response.setHeader("Access-Control-Allow-Headers", String.join(",", allowedHeaders));
        }
        
        response.setHeader("Access-Control-Allow-Credentials", String.valueOf(allowCredentials));
        response.setHeader("Access-Control-Max-Age", String.valueOf(maxAge));
        
        // Expose headers that the frontend might need
        response.setHeader("Access-Control-Expose-Headers", 
                "Authorization,Content-Type,X-Total-Count,X-Page-Number,X-Page-Size,Location");

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("Handling CORS preflight request for: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isOriginAllowed(String origin) {
        List<String> allowedOriginsList = Arrays.asList(allowedOrigins);
        return allowedOriginsList.contains(origin) || allowedOriginsList.contains("*");
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Initializing Global CORS Filter with allowed origins: {}", Arrays.toString(allowedOrigins));
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
