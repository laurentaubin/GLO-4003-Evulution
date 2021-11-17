package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.api.delivery.CompletedDeliveryResponseAssembler;
import ca.ulaval.glo4003.ws.api.delivery.DeliveryDestinationAssembler;
import ca.ulaval.glo4003.ws.api.delivery.DeliveryResource;
import ca.ulaval.glo4003.ws.api.delivery.DeliveryResourceImpl;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryFactory;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryRepository;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionRepository;
import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentService;
import ca.ulaval.glo4003.ws.domain.transaction.payment.ReceiptFactory;
import ca.ulaval.glo4003.ws.domain.user.OwnershipHandler;
import ca.ulaval.glo4003.ws.infrastructure.delivery.InMemoryDeliveryRepository;
import ca.ulaval.glo4003.ws.infrastructure.transaction.InMemoryTransactionRepository;
import ca.ulaval.glo4003.ws.infrastructure.transaction.TransactionAssembler;
import jakarta.validation.Validation;

public class DeliveryContext implements Context {
  private static final Integer AMOUNT_OF_YEARS_TO_PAY_OVER = 6;
  public static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerRepositories();
    registerServices();
    registerResources();
  }

  private void registerRepositories() {
    serviceLocator.register(TransactionAssembler.class, new TransactionAssembler());
    serviceLocator.register(
        TransactionRepository.class,
        new InMemoryTransactionRepository(serviceLocator.resolve(TransactionAssembler.class)));
    serviceLocator.register(DeliveryRepository.class, new InMemoryDeliveryRepository());
  }

  private void registerServices() {
    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    serviceLocator.register(DeliveryDestinationAssembler.class, new DeliveryDestinationAssembler());
    serviceLocator.register(
        DeliveryRequestValidator.class, new DeliveryRequestValidator(validator));
    serviceLocator.register(DeliveryFactory.class, new DeliveryFactory());
    serviceLocator.register(
        CompletedDeliveryResponseAssembler.class, new CompletedDeliveryResponseAssembler());

    serviceLocator.register(ReceiptFactory.class, new ReceiptFactory(AMOUNT_OF_YEARS_TO_PAY_OVER));
    serviceLocator.register(
        PaymentService.class,
        new PaymentService(
            serviceLocator.resolve(TransactionRepository.class),
            serviceLocator.resolve(ReceiptFactory.class)));

    DeliveryService deliveryService =
        new DeliveryService(
            serviceLocator.resolve(DeliveryFactory.class),
            serviceLocator.resolve(DeliveryRepository.class),
            serviceLocator.resolve(PaymentService.class));

    serviceLocator.register(DeliveryService.class, deliveryService);
  }

  private void registerResources() {
    serviceLocator.register(
        DeliveryResource.class,
        new DeliveryResourceImpl(
            serviceLocator.resolve(DeliveryService.class),
            serviceLocator.resolve(DeliveryRequestValidator.class),
            serviceLocator.resolve(DeliveryDestinationAssembler.class),
            serviceLocator.resolve(CompletedDeliveryResponseAssembler.class),
            serviceLocator.resolve(OwnershipHandler.class),
            serviceLocator.resolve(RoleHandler.class)));
  }
}
