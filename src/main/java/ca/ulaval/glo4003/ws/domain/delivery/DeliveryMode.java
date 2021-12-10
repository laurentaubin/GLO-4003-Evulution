package ca.ulaval.glo4003.ws.domain.delivery;

import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidDeliveryModeException;

import java.util.Arrays;
import java.util.stream.Collectors;

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
    throw new InvalidDeliveryModeException(
        Arrays.stream(DeliveryMode.values())
            .map(DeliveryMode::toString)
            .collect(Collectors.toSet()));
  }

  public String getDeliveryMode() {
    return deliveryMode;
  }
}
