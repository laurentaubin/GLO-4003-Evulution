package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import java.util.Random;

public class VehicleAssemblyPlanner extends VehicleAssemblyDelayObservable {
  private Random randomDelay;

  public VehicleAssemblyPlanner(Random randomDelay) {
    this.randomDelay = randomDelay;
  }

  public VehicleAssemblyProductionTime getProductionTime(Order order) {
    if (isDelayed()) {
      notifyVehicleAssemblyDelay(order);
      return VehicleAssemblyProductionTime.DELAYED;
    }
    return VehicleAssemblyProductionTime.NORMAL;
  }

  private boolean isDelayed() {
    return randomDelay.nextBoolean();
  }
}
