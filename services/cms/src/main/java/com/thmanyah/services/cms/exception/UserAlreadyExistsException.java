package com.thmanyah.services.cms.exception;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String email) {
    super("User already exists with email: " + email);
  }
}
