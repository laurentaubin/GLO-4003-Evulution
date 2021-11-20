package ca.ulaval.glo4003.ws.domain.user.exception;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;

public class NoTransactionLinkedToDeliveryException extends RuntimeException {
  private final DeliveryId deliveryId;

  public NoTransactionLinkedToDeliveryException(DeliveryId deliveryId) {
    this.deliveryId = deliveryId;
  }

  public DeliveryId getDeliveryId() {
    return deliveryId;
  }
}
