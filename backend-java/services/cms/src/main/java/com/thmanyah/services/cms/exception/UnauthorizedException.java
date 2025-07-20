package com.thmanyah.services.cms.exception;

public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException(String message) {
    super(message);
  }

  public UnauthorizedException(String operation, String requiredRole) {
    super("Access denied. Operation '" + operation + "' requires role: " + requiredRole);
  }
}
