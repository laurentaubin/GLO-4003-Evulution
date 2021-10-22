package ca.ulaval.glo4003.ws.domain.delivery;

import java.util.Objects;
import java.util.UUID;

public class DeliveryId {
  private final String deliveryId;

  public DeliveryId(String deliveryId) {
    this.deliveryId = deliveryId;
  }

  public DeliveryId() {
    this.deliveryId = UUID.randomUUID().toString();
  }

  public String getDeliveryId() {
    return deliveryId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeliveryId that = (DeliveryId) o;
    return deliveryId.equals(that.deliveryId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deliveryId);
  }

  @Override
  public String toString() {
    return deliveryId;
  }
}
