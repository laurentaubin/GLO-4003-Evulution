package ca.ulaval.glo4003.ws.domain.delivery.exception;

public class InvalidLocationException extends RuntimeException {

  public static String error;
  public static String description;

  public InvalidLocationException() {
    this.error = "INVALID_CAMPUS_LOCATION";
    this.description = "Location must be inside a valid building.";
  }
}
