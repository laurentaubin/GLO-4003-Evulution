package ca.ulaval.glo4003.ws.domain.manufacturer;

public class ManufacturerScheduler extends ManufacturerStateObservable {
  private final PeriodicManufacturer modelManufacturer;
  private final PeriodicManufacturer batteryManufacturer;
  private final PeriodicManufacturer vehicleManufacturer;

  private boolean shutdown = false;

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
