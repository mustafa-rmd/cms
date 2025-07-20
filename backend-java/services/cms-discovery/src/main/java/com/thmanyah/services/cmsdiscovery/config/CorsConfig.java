package com.thmanyah.services.cmsdiscovery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration for the CMS Discovery service
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cms-discovery.cors.allowed-origins:http://localhost:4200,http://localhost:3000,http://127.0.0.1:4200,http://127.0.0.1:3000}")
    private String[] allowedOrigins;

    @Value("${cms-discovery.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cms-discovery.cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${cms-discovery.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cms-discovery.cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);

        // Also allow CORS for Swagger UI
        registry.addMapping("/swagger-ui/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(maxAge);

        // Allow CORS for OpenAPI docs
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(maxAge);

        // Allow CORS for Eureka endpoints
        registry.addMapping("/eureka/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(maxAge);

        // Allow CORS for actuator endpoints
        registry.addMapping("/actuator/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(maxAge);
    }

    /**
     * CORS configuration source for Spring Security (if needed)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        
        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        
        // Set allowed headers
        if (allowedHeaders.length == 1 && "*".equals(allowedHeaders[0])) {
            configuration.addAllowedHeader("*");
        } else {
            configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        }
        
        // Set exposed headers (for service discovery info, etc.)
        configuration.setExposedHeaders(Arrays.asList(
                "Content-Type",
                "X-Total-Count",
                "X-Service-Count",
                "X-Instance-Count",
                "Location"
        ));
        
        // Allow credentials
        configuration.setAllowCredentials(allowCredentials);
        
        // Set max age
        configuration.setMaxAge(maxAge);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
