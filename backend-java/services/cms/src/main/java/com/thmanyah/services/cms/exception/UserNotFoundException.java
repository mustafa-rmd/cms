package com.thmanyah.services.cms.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(UUID id) {
    super("User not found with ID: " + id);
  }

  public UserNotFoundException(String message) {
    super(message);
  }
}
