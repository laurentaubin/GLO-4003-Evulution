package ca.ulaval.glo4003.ws.domain.delivery;

import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidDeliveryModeException;

public enum DeliveryMode {
  CAMPUS("At campus");

  private final String deliveryMode;

  DeliveryMode(String deliveryMode) {
    this.deliveryMode = deliveryMode;
  }

  public static DeliveryMode fromString(String value) {
    for (DeliveryMode mode : DeliveryMode.values()) {
      if (mode.deliveryMode.equalsIgnoreCase(value)) {
        return mode;
      }
    }
    throw new InvalidDeliveryModeException();
  }

  public String getDeliveryMode() {
    return deliveryMode;
  }
}
