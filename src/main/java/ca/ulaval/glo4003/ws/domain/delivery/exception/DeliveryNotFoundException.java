package ca.ulaval.glo4003.ws.domain.delivery.exception;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;

public class DeliveryNotFoundException extends RuntimeException {

  public static String error;
  public static String description;

  public DeliveryNotFoundException(DeliveryId deliveryId) {
    this.error = "DELIVERY_NOT_FOUND";
    this.description = String.format("Could not find delivery with id %s", deliveryId);
  }
}
