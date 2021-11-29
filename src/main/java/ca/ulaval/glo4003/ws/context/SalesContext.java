package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionCompletedObservable;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionFactory;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;

public class SalesContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerServices();
  }

  private void registerServices() {
    // TODO extend TransactionCompleted Observable instead of injecting
    TransactionCompletedObservable txCompletedObservable = new TransactionCompletedObservable();
    serviceLocator.register(TransactionCompletedObservable.class, txCompletedObservable);
    txCompletedObservable.register(serviceLocator.resolve(AssemblyLine.class));

    serviceLocator.register(TransactionFactory.class, new TransactionFactory());
    serviceLocator.register(VehicleFactory.class, new VehicleFactory());

    serviceLocator.register(TransactionService.class, new TransactionService());
  }
}
