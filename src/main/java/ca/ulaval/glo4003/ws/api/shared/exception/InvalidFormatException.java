package ca.ulaval.glo4003.ws.api.shared.exception;

public class InvalidFormatException extends RuntimeException {

  public static final String error = "INVALID_FORMAT";
  public static String description = "bad input parameter";

  public InvalidFormatException(String description) {
    InvalidFormatException.description = description;
  }
}
