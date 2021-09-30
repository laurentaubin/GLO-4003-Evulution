package ca.ulaval.glo4003.ws.infrastructure.delivery;

import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryRepository;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DuplicateDeliveryException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryDeliveryRepository implements DeliveryRepository {
  private final InMemoryDeliveryDtoAssembler assembler = new InMemoryDeliveryDtoAssembler();
  private final Map<DeliveryId, InMemoryDeliveryDto> deliveries = new HashMap<>();

  @Override
  public Delivery find(DeliveryId deliveryId) {
    Optional<InMemoryDeliveryDto> deliveryDto = Optional.ofNullable(deliveries.get(deliveryId));
    if (deliveryDto.isPresent()) {
      return assembler.assemble(deliveryDto.get());
    }
    throw new DeliveryNotFoundException(deliveryId);
  }

  @Override
  public void save(Delivery delivery) {
    if (deliveries.containsKey(delivery.getDeliveryId())) {
      throw new DuplicateDeliveryException(delivery.getDeliveryId());
    }
    deliveries.put(delivery.getDeliveryId(), assembler.assemble(delivery));
  }

  @Override
  public void update(Delivery delivery) {
    Optional<InMemoryDeliveryDto> foundDeliveryDto =
        Optional.ofNullable(deliveries.get(delivery.getDeliveryId()));
    if (foundDeliveryDto.isPresent()) {
      deliveries.put(delivery.getDeliveryId(), assembler.assemble(delivery));
    } else {
      throw new DeliveryNotFoundException(delivery.getDeliveryId());
    }
  }
}
