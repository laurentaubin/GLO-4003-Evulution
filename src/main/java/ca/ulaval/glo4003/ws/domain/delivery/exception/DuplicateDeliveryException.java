package ca.ulaval.glo4003.ws.domain.delivery.exception;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;

public class DuplicateDeliveryException extends RuntimeException {
  private final DeliveryId deliveryId;

  public DuplicateDeliveryException(DeliveryId deliveryId) {
    this.deliveryId = deliveryId;
  }

  public DeliveryId getDeliveryId() {
    return deliveryId;
  }
}
