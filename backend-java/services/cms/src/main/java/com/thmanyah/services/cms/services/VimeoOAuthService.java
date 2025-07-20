package com.thmanyah.services.cms.services;

/**
 * Service interface for Vimeo OAuth operations. Handles OAuth token generation and configuration
 * validation for Vimeo API access.
 */
public interface VimeoOAuthService {

  /**
   * Get access token for Vimeo API
   *
   * @return Access token string
   * @throws com.thmanyah.services.cms.exception.ExternalProviderException if token generation fails
   */
  String getAccessToken();

  /**
   * Check if Vimeo OAuth is properly configured
   *
   * @return true if client ID and secret are configured, false otherwise
   */
  boolean isConfigured();
}
