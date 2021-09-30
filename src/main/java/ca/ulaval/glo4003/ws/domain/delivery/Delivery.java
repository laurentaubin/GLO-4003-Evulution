package ca.ulaval.glo4003.ws.domain.delivery;

import java.util.Objects;

public class Delivery {
  DeliveryId deliveryId;
  DeliveryDestination deliveryDestination;

  public Delivery() {
    this.deliveryId = new DeliveryId();
  }

  public Delivery(DeliveryId deliveryId) {
    this.deliveryId = deliveryId;
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

  public void setDeliveryLocation(DeliveryDestination deliveryDestination) {
    this.deliveryDestination = deliveryDestination;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }

    if (!(other instanceof Delivery)) {
      return false;
    }

    Delivery otherDelivery = (Delivery) other;
    return deliveryId.equals(otherDelivery.deliveryId)
        && deliveryDestination.equals(otherDelivery.deliveryDestination);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deliveryId, deliveryDestination);
  }
}
