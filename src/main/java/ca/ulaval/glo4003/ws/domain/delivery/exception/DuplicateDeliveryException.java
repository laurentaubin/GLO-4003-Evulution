package ca.ulaval.glo4003.ws.domain.delivery.exception;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;

public class DuplicateDeliveryException extends RuntimeException {
  public static String error;
  public static String description;

  public DuplicateDeliveryException(DeliveryId deliveryId) {
    this.error = "DUPLICATE_DELIVERY";
    this.description = String.format("delivery with id %s already in repository", deliveryId);
  }
}
