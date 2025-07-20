package com.thmanyah.services.cms.exception;

public class ShowAlreadyExistsException extends RuntimeException {

  public ShowAlreadyExistsException(String title) {
    super(String.format("Show with title '%s' already exists", title));
  }

  public ShowAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
