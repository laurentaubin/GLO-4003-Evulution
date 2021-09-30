package ca.ulaval.glo4003.ws.domain.battery;

public class InvalidBatteryException extends RuntimeException {

  public static String error;
  public static String description;

  public InvalidBatteryException() {
    this.error = "INVALID_BATTERY_TYPE";
    this.description = "battery must be of type STANDARD, LONG_RANGE or SHORT_RANGE";
  }
}
