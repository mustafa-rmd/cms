package com.thmanyah.services.cms.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** CORS configuration for the CMS service */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Value(
      "${cms.cors.allowed-origins:http://localhost:4200,http://localhost:3000,http://127.0.0.1:4200,http://127.0.0.1:3000}")
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
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/api/**")
        .allowedOrigins(allowedOrigins)
        .allowedMethods(allowedMethods)
        .allowedHeaders(allowedHeaders)
        .allowCredentials(allowCredentials)
        .maxAge(maxAge);

    // Also allow CORS for Swagger UI
    registry
        .addMapping("/swagger-ui/**")
        .allowedOrigins(allowedOrigins)
        .allowedMethods("GET", "POST", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(false)
        .maxAge(maxAge);

    // Allow CORS for OpenAPI docs
    registry
        .addMapping("/v3/api-docs/**")
        .allowedOrigins(allowedOrigins)
        .allowedMethods("GET", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(false)
        .maxAge(maxAge);
  }

  /** CORS configuration source for Spring Security */
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

    // Set exposed headers (for JWT tokens, pagination info, etc.)
    configuration.setExposedHeaders(
        Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count",
            "X-Page-Number",
            "X-Page-Size",
            "Location"));

    // Allow credentials
    configuration.setAllowCredentials(allowCredentials);

    // Set max age
    configuration.setMaxAge(maxAge);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
