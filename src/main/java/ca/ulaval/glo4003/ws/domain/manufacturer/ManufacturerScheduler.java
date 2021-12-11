package ca.ulaval.glo4003.ws.domain.manufacturer;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.manufacturer.battery.BatteryManufacturerImpl;
import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelManufacturerImpl;
import ca.ulaval.glo4003.ws.domain.manufacturer.vehicle.VehicleManufacturerImpl;

public class ManufacturerScheduler extends ManufacturerStateObservable {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final PeriodicManufacturer modelManufacturer;
  private final PeriodicManufacturer batteryManufacturer;
  private final PeriodicManufacturer vehicleManufacturer;

  private boolean shutdown = false;

  public ManufacturerScheduler() {
    this(
        serviceLocator.resolve(ModelManufacturerImpl.class),
        serviceLocator.resolve(BatteryManufacturerImpl.class),
        serviceLocator.resolve(VehicleManufacturerImpl.class));
  }

  public ManufacturerScheduler(
      PeriodicManufacturer modelManufacturer,
      PeriodicManufacturer batteryManufacturer,
      PeriodicManufacturer vehicleManufacturer) {
    this.modelManufacturer = modelManufacturer;
    this.batteryManufacturer = batteryManufacturer;
    this.vehicleManufacturer = vehicleManufacturer;
  }

  public void advanceTime() {
    if (shutdown) return;
    vehicleManufacturer.advanceTime();
    batteryManufacturer.advanceTime();
    modelManufacturer.advanceTime();
  }

  public void shutdown() {
    if (!shutdown) {
      batteryManufacturer.stop();
      modelManufacturer.stop();
      vehicleManufacturer.stop();
      notifyShutdown();
    }
    shutdown = true;
  }

  public void reactive() {
    if (shutdown) {
      notifyReactivation();
    }
    shutdown = false;
  }
}
