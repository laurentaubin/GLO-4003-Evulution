package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.transaction.TransactionCompletedObservable;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionFactory;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
import ca.ulaval.glo4003.ws.service.assembly.AssemblyLineService;
import ca.ulaval.glo4003.ws.service.transaction.TransactionService;

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
    txCompletedObservable.register(serviceLocator.resolve(AssemblyLineService.class));

    serviceLocator.register(TransactionFactory.class, new TransactionFactory());
    serviceLocator.register(VehicleFactory.class, new VehicleFactory());
    serviceLocator.register(TransactionService.class, new TransactionService());
  }
}
