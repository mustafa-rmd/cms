package com.thmanyah.services.cms.config;

import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.services.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final AuthenticationService authenticationService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;

    // Skip JWT validation for public endpoints
    if (isPublicEndpoint(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    // Check if Authorization header is present and starts with "Bearer "
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.debug("No valid Authorization header found for: {}", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.substring(7); // Remove "Bearer " prefix

    try {
      // Get user from token
      User user = authenticationService.getUserFromToken(jwt);

      // Set authentication in security context
      if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        // Create authorities based on user role
        List<SimpleGrantedAuthority> authorities =
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Create authentication token
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set user in request attribute for easy access in controllers
        request.setAttribute("currentUser", user);
        request.setAttribute("currentUserEmail", user.getEmail());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        log.debug("Authentication set for user: {} with role: {}", user.getEmail(), user.getRole());
      }

    } catch (Exception e) {
      log.debug("JWT authentication failed: {}", e.getMessage());
      // Don't set authentication - let the request proceed without auth
    }

    filterChain.doFilter(request, response);
  }

  /** Check if the endpoint is public (doesn't require authentication) */
  private boolean isPublicEndpoint(String uri) {
    return uri.startsWith("/actuator/")
        || uri.startsWith("/swagger-ui/")
        || uri.startsWith("/v3/api-docs/")
        || uri.equals("/swagger-ui.html")
        || uri.startsWith("/api/v1/auth/");
  }
}
