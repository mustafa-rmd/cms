package com.thmanyah.services.cmsdiscovery.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Global controller advice for handling CORS and common exceptions in CMS Discovery service
 */
@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    /**
     * Handle CORS preflight requests that might not be caught by other handlers
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> handleNoHandlerFound(
            NoHandlerFoundException ex, 
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        // Check if this is a CORS preflight request
        if (CorsUtils.isPreFlightRequest(request)) {
            log.debug("Handling CORS preflight request for: {}", request.getRequestURI());
            return ResponseEntity.ok().build();
        }
        
        // For non-CORS requests, return 404
        return ResponseEntity.notFound().build();
    }

    /**
     * Handle general exceptions and ensure CORS headers are present
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception ex, 
            HttpServletRequest request) {
        
        log.error("Unhandled exception in CMS Discovery request to {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("service", "cms-discovery");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
