package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;

import java.util.Random;

public class VehicleAssemblyPlanner extends VehicleAssemblyDelayObservable {
  private final Random randomDelay;

  public VehicleAssemblyPlanner(Random randomDelay) {
    this.randomDelay = randomDelay;
  }

  public ProductionTime getProductionTime(Order order) {
    if (isDelayed()) {
      notifyVehicleAssemblyDelay(order);
      return VehicleAssemblyProductionTime.DELAYED.getProductionTime();
    }
    return VehicleAssemblyProductionTime.NORMAL.getProductionTime();
  }

  private boolean isDelayed() {
    return randomDelay.nextBoolean();
  }
}
