package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.manufacturer.ManufacturerScheduler;
import ca.ulaval.glo4003.ws.domain.manufacturer.vehicle.VehicleManufacturerImpl;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.warehouse.strategy.LinearWarehouseStrategy;
import ca.ulaval.glo4003.ws.infrastructure.manufacturer.battery.BatteryManufacturerImpl;
import ca.ulaval.glo4003.ws.infrastructure.manufacturer.model.ModelManufacturerImpl;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;
import ca.ulaval.glo4003.ws.service.user.UserService;

import java.util.List;

public class ManufacturerContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    ModelManufacturerImpl modelManufacturer = serviceLocator.resolve(ModelManufacturerImpl.class);
    BatteryManufacturerImpl batteryManufacturer =
        serviceLocator.resolve(BatteryManufacturerImpl.class);
    VehicleManufacturerImpl vehicleManufacturer =
        serviceLocator.resolve(VehicleManufacturerImpl.class);
    ManufacturerScheduler manufacturerScheduler =
        new ManufacturerScheduler(modelManufacturer, batteryManufacturer, vehicleManufacturer);

    manufacturerScheduler.registerShutdownObserver(
        serviceLocator.resolve(LinearWarehouseStrategy.class));
    manufacturerScheduler.registerReactivateObserver(
        serviceLocator.resolve(LinearWarehouseStrategy.class));

    ManufacturerService manufacturerService = new ManufacturerService(manufacturerScheduler, serviceLocator.resolve(UserService.class));

    serviceLocator.register(ManufacturerScheduler.class, manufacturerScheduler);
    serviceLocator.register(ManufacturerService.class, manufacturerService);
  }
}
