package ca.ulaval.glo4003.ws.domain.delivery;

public class DeliveryDestination {
  private final DeliveryMode deliveryMode;
  private final Location location;

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
