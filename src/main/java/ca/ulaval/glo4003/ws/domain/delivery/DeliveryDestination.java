package ca.ulaval.glo4003.ws.domain.delivery;

public class DeliveryDestination {
  private DeliveryMode deliveryMode;
  private Location location;

  public DeliveryDestination(DeliveryMode deliveryMode, Location location) {
    this.deliveryMode = deliveryMode;
    this.location = location;
  }

  public DeliveryMode getMode() {
    return deliveryMode;
  }

  public Location getLocation() {
    return location;
  }
}
