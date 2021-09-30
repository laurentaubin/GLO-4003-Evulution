package ca.ulaval.glo4003.ws.infrastructure.delivery;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;

public class InMemoryDeliveryDto {
  DeliveryId deliveryId;
  DeliveryDestination deliveryDestination;

  public InMemoryDeliveryDto(DeliveryId deliveryId, DeliveryDestination deliveryDestination) {
    this.deliveryId = deliveryId;
    this.deliveryDestination = deliveryDestination;
  }

  public DeliveryId getDeliveryId() {
    return deliveryId;
  }

  public void setDeliveryId(DeliveryId deliveryId) {
    this.deliveryId = deliveryId;
  }

  public DeliveryDestination getDeliveryLocation() {
    return deliveryDestination;
  }
}
