package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionCompletedObservable;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionFactory;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;

public class SalesContext implements Context {
  public static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerServices();
  }

  private void registerServices() {
    // TODO extend TransactionCompleted Observable instead of injecting
    TransactionCompletedObservable transactionCompletedObservable =
        new TransactionCompletedObservable();
    serviceLocator.register(TransactionCompletedObservable.class, transactionCompletedObservable);
    transactionCompletedObservable.register(serviceLocator.resolve(AssemblyLine.class));

    serviceLocator.register(TransactionFactory.class, new TransactionFactory());
    serviceLocator.register(
        VehicleFactory.class, new VehicleFactory(serviceLocator.resolve(ModelRepository.class)));

    serviceLocator.register(TransactionService.class, new TransactionService());
  }
}
