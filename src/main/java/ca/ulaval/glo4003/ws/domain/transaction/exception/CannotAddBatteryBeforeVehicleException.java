package ca.ulaval.glo4003.ws.domain.transaction.exception;

public class CannotAddBatteryBeforeVehicleException extends RuntimeException {
  private static String error = "CANNOT_ADD_BATTERY_BEFORE_VEHICLE";
  private static String description =
      "A vehicle must be added to the transaction before adding a battery";

  public CannotAddBatteryBeforeVehicleException() {
    super();
  }

  public String getDescription() {
    return description;
  }

  public String getError() {
    return error;
  }
}
