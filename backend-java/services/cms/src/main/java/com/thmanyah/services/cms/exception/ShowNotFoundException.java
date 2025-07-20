package com.thmanyah.services.cms.exception;

import java.util.UUID;

public class ShowNotFoundException extends RuntimeException {

  public ShowNotFoundException(UUID id) {
    super(String.format("Show with id: %s doesn't exist", id));
  }

  public ShowNotFoundException(String message) {
    super(message);
  }
}
