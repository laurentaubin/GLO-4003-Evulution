package ca.ulaval.glo4003.ws.domain.delivery;

public interface DeliveryRepository {
  Delivery find(DeliveryId deliveryId);

  void save(Delivery delivery);

  void update(Delivery delivery);
}
