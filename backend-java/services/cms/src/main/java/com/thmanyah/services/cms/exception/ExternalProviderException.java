package com.thmanyah.services.cms.exception;

import lombok.Getter;

@Getter
public class ExternalProviderException extends RuntimeException {

  private final String providerName;

  public ExternalProviderException(String providerName, String message) {
    super(String.format("Provider '%s': %s", providerName, message));
    this.providerName = providerName;
  }

  public ExternalProviderException(String providerName, String message, Throwable cause) {
    super(String.format("Provider '%s': %s", providerName, message), cause);
    this.providerName = providerName;
  }
}
