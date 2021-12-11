package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.manufacturer.ManufacturerScheduler;
import ca.ulaval.glo4003.ws.domain.warehouse.strategy.LinearWarehouseStrategy;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;
import ca.ulaval.glo4003.ws.service.user.UserService;

public class ManufacturerContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    ManufacturerScheduler manufacturerScheduler = new ManufacturerScheduler();

    manufacturerScheduler.registerShutdownObserver(
        serviceLocator.resolve(LinearWarehouseStrategy.class));
    manufacturerScheduler.registerReactivateObserver(
        serviceLocator.resolve(LinearWarehouseStrategy.class));

    ManufacturerService manufacturerService =
        new ManufacturerService(manufacturerScheduler, serviceLocator.resolve(UserService.class));

    serviceLocator.register(ManufacturerScheduler.class, manufacturerScheduler);
    serviceLocator.register(ManufacturerService.class, manufacturerService);
  }
}
