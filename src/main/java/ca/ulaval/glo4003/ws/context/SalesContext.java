package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.transaction.CreatedTransactionResponseAssembler;
import ca.ulaval.glo4003.ws.api.transaction.PaymentRequestAssembler;
import ca.ulaval.glo4003.ws.api.transaction.TransactionResource;
import ca.ulaval.glo4003.ws.api.transaction.TransactionResourceImpl;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.PaymentRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryOwnershipHandler;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.transaction.*;
import ca.ulaval.glo4003.ws.domain.user.TransactionOwnershipHandler;
import ca.ulaval.glo4003.ws.domain.vehicle.ModelRepository;
import ca.ulaval.glo4003.ws.infrastructure.transaction.InMemoryTransactionRepository;
import jakarta.validation.Validation;

public class SalesContext implements Context {
  public static ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerRepositories();
    registerServices();
    registerResources();
  }

  private void registerRepositories() {
    serviceLocator.register(TransactionRepository.class, new InMemoryTransactionRepository());
  }

  private void registerServices() {
    var validator = Validation.buildDefaultValidatorFactory().getValidator();

    serviceLocator.register(BankAccountFactory.class, new BankAccountFactory());
    serviceLocator.register(
        PaymentRequestAssembler.class,
        new PaymentRequestAssembler(serviceLocator.resolve(BankAccountFactory.class)));
    serviceLocator.register(
        CreatedTransactionResponseAssembler.class, new CreatedTransactionResponseAssembler());
    serviceLocator.register(TransactionFactory.class, new TransactionFactory());
    serviceLocator.register(VehicleRequestValidator.class, new VehicleRequestValidator(validator));
    serviceLocator.register(PaymentRequestValidator.class, new PaymentRequestValidator(validator));
    serviceLocator.register(BatteryRequestValidator.class, new BatteryRequestValidator(validator));
    serviceLocator.register(
        VehicleFactory.class, new VehicleFactory(serviceLocator.resolve(ModelRepository.class)));

    serviceLocator.register(
        TransactionService.class,
        new TransactionService(
            serviceLocator.resolve(TransactionRepository.class),
            serviceLocator.resolve(TransactionFactory.class),
            serviceLocator.resolve(BatteryRepository.class)));
  }

  private void registerResources() {
    serviceLocator.register(
        TransactionResource.class,
        new TransactionResourceImpl(
            serviceLocator.resolve(TransactionService.class),
            serviceLocator.resolve(DeliveryService.class),
            serviceLocator.resolve(TransactionOwnershipHandler.class),
            serviceLocator.resolve(DeliveryOwnershipHandler.class),
            serviceLocator.resolve(CreatedTransactionResponseAssembler.class),
            serviceLocator.resolve(VehicleRequestValidator.class),
            serviceLocator.resolve(RoleHandler.class),
            serviceLocator.resolve(BatteryRequestValidator.class),
            serviceLocator.resolve(PaymentRequestAssembler.class),
            serviceLocator.resolve(PaymentRequestValidator.class),
            serviceLocator.resolve(VehicleFactory.class)));
  }
}
