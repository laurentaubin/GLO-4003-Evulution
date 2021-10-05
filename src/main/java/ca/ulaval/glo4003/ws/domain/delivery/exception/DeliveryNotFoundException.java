package ca.ulaval.glo4003.ws.domain.delivery.exception;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;

public class DeliveryNotFoundException extends RuntimeException {
  private final DeliveryId deliveryId;

  public DeliveryNotFoundException(DeliveryId deliveryId) {
    this.deliveryId = deliveryId;
  }

  public DeliveryId getDeliveryId() {
    return deliveryId;
  }
}
