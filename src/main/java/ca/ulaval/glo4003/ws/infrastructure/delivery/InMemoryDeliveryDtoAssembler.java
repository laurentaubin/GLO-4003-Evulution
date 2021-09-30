package ca.ulaval.glo4003.ws.infrastructure.delivery;

import ca.ulaval.glo4003.ws.domain.delivery.Delivery;

public class InMemoryDeliveryDtoAssembler {
  public InMemoryDeliveryDto assemble(Delivery delivery) {
    return new InMemoryDeliveryDto(delivery.getDeliveryId(), delivery.getDeliveryLocation());
  }

  public Delivery assemble(InMemoryDeliveryDto deliveryDto) {
    Delivery delivery = new Delivery(deliveryDto.getDeliveryId());
    delivery.setDeliveryLocation(deliveryDto.getDeliveryLocation());
    return delivery;
  }
}
