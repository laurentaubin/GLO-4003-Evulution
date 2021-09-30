package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryFactory;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryRepository;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.infrastructure.delivery.InMemoryDeliveryRepository;

public class ApiContext {
  public static ServiceLocator serviceLocator = ServiceLocator.getInstance();

  public ApiContext() {
    serviceLocator.register(DeliveryRepository.class, new InMemoryDeliveryRepository());
  }

  public void applyContext() {
    applyDeliveryContext();
  }

  private void applyDeliveryContext() {
    serviceLocator.register(DeliveryFactory.class, new DeliveryFactory());
    serviceLocator.register(
        DeliveryService.class,
        new DeliveryService(
            serviceLocator.resolve(DeliveryFactory.class),
            serviceLocator.resolve(DeliveryRepository.class)));
  }
}
