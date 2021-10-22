package ca.ulaval.glo4003.ws.domain.vehicle.exception;

public class InvalidOperationException extends RuntimeException {
  private final String description;

  public InvalidOperationException(String description) {
    this.description = description;
  }
}
