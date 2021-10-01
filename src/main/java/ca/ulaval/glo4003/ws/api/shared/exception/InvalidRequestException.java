package ca.ulaval.glo4003.ws.api.shared.exception;

public class InvalidRequestException extends RuntimeException {
  public static final String error = "INVALID_REQUEST";
  public static String description = "invalid request";
}
