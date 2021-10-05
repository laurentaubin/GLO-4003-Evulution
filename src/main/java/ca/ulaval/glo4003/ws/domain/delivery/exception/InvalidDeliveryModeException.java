package ca.ulaval.glo4003.ws.domain.delivery.exception;

import java.util.Set;

public class InvalidDeliveryModeException extends RuntimeException {
  private final Set<String> deliveryModes;

  public InvalidDeliveryModeException(Set<String> deliveryModes) {
    this.deliveryModes = deliveryModes;
  }

  public Set<String> getDeliveryModes() {
    return deliveryModes;
  }
}
