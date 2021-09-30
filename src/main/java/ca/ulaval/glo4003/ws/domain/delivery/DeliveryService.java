package ca.ulaval.glo4003.ws.domain.delivery;

public class DeliveryService {
  private final DeliveryFactory deliveryFactory;
  private final DeliveryRepository deliveryRepository;

  public DeliveryService(DeliveryFactory deliveryFactory, DeliveryRepository deliveryRepository) {
    this.deliveryFactory = deliveryFactory;
    this.deliveryRepository = deliveryRepository;
  }

  public Delivery createDelivery() {
    Delivery delivery = deliveryFactory.createDelivery();
    deliveryRepository.save(delivery);
    return delivery;
  }

  public void addDeliveryDestination(
      DeliveryId deliveryId, DeliveryDestination deliveryDestination) {
    Delivery delivery = deliveryRepository.find(deliveryId);
    delivery.setDeliveryLocation(deliveryDestination);
    deliveryRepository.update(delivery);
  }
}
