package ca.ulaval.glo4003.ws.api.customer.exception;

public class InvalidFormatException extends RuntimeException {
  private static final String error = "INVALID_FORMAT";
  private static String description = "Invalid Format";

  public InvalidFormatException() {}

  public InvalidFormatException(String description) {
    InvalidFormatException.description = description;
  }

  public String getError() {
    return error;
  }

  public String getDescription() {
    return description;
  }
}
