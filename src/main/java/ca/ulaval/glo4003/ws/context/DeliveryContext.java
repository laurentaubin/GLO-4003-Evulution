package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.api.delivery.DeliveryDestinationAssembler;
import ca.ulaval.glo4003.ws.api.delivery.DeliveryResource;
import ca.ulaval.glo4003.ws.api.delivery.DeliveryResourceImpl;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryFactory;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryOwnershipHandler;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryRepository;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.infrastructure.delivery.InMemoryDeliveryRepository;
import jakarta.validation.Validation;

public class DeliveryContext implements Context {
  public static ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerRepositories();
    registerServices();
    registerResources();
  }

  private void registerRepositories() {
    serviceLocator.register(DeliveryRepository.class, new InMemoryDeliveryRepository());
  }

  private void registerServices() {
    var validator = Validation.buildDefaultValidatorFactory().getValidator();

    serviceLocator.register(DeliveryDestinationAssembler.class, new DeliveryDestinationAssembler());
    serviceLocator.register(
        DeliveryRequestValidator.class, new DeliveryRequestValidator(validator));
    serviceLocator.register(DeliveryFactory.class, new DeliveryFactory());

    var deliveryService =
        new DeliveryService(
            serviceLocator.resolve(DeliveryFactory.class),
            serviceLocator.resolve(DeliveryRepository.class));

    serviceLocator.register(DeliveryService.class, deliveryService);
  }

  private void registerResources() {
    serviceLocator.register(
        DeliveryResource.class,
        new DeliveryResourceImpl(
            serviceLocator.resolve(DeliveryService.class),
            serviceLocator.resolve(DeliveryRequestValidator.class),
            serviceLocator.resolve(DeliveryDestinationAssembler.class),
            serviceLocator.resolve(DeliveryOwnershipHandler.class),
            serviceLocator.resolve(RoleHandler.class)));
  }
}
