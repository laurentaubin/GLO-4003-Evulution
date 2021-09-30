package ca.ulaval.glo4003.ws.domain.delivery.exception;

public class InvalidDeliveryModeException extends RuntimeException {
  public static String error;
  public static String description;

  public InvalidDeliveryModeException() {
    this.error = "INVALID_DELIVERY_MODE";
    this.description = "The selected delivery mode is invalid.";
  }
}
