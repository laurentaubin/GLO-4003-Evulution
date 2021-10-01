package ca.ulaval.glo4003.ws.domain.delivery.exception;

public class InvalidLocationException extends RuntimeException {

  private final String error;
  private final String description;

  public InvalidLocationException() {
    this.error = "INVALID_CAMPUS_LOCATION";
    this.description = "Location must be inside a valid building.";
  }

  public String getDescription() {
    return description;
  }

  public String getError() {
    return error;
  }
}
