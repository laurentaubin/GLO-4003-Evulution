package ca.ulaval.glo4003.ws.domain.vehicle.exception;

public class InvalidOperationException extends RuntimeException {

  public InvalidOperationException(String description) {
    super(description);
  }
}
