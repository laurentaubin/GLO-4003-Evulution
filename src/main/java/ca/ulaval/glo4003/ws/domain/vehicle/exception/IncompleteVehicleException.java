package ca.ulaval.glo4003.ws.domain.vehicle.exception;

public class IncompleteVehicleException extends RuntimeException {
  public IncompleteVehicleException(String description) {
    super(description);
  }
}
