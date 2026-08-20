package com.crm.exception;

public class DuplicateStaffException extends RuntimeException {

  public DuplicateStaffException(String message) {
    super(message);
  }
}