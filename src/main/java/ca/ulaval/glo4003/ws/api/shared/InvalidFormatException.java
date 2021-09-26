package ca.ulaval.glo4003.ws.api.shared;

public class InvalidFormatException extends RuntimeException {

  public static final String error = "INVALID_FORMAT";
  public static String description = "Invalid Format";

  public InvalidFormatException(String description) {
    InvalidFormatException.description = description;
  }
}
