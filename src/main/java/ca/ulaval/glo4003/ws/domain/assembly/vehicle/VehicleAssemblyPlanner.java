package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.shared.RandomProvider;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VehicleAssemblyPlanner extends VehicleAssemblyDelayObservable {
  private static final Logger LOGGER = LogManager.getLogger();

  private final RandomProvider randomProvider;

  public VehicleAssemblyPlanner(RandomProvider randomProvider) {
    this.randomProvider = randomProvider;
  }

  public ProductionTime getNormalAssemblyTime() {
    return VehicleAssemblyProductionTime.NORMAL.getProductionTime();
  }

  public ProductionTime getProductionTime(Order order) {
    if (isDelayed()) {
      LOGGER.info(String.format("Random delay for order %s", order.getId()));
      ProductionTime assemblyDelay =
          VehicleAssemblyProductionTime.DELAYED
              .getProductionTime()
              .subtract(VehicleAssemblyProductionTime.NORMAL.getProductionTime());
      order.addAssemblyDelay(assemblyDelay);
      notifyVehicleAssemblyDelay(order);
      return VehicleAssemblyProductionTime.DELAYED.getProductionTime();
    }
    return VehicleAssemblyProductionTime.NORMAL.getProductionTime();
  }

  private boolean isDelayed() {
    return randomProvider.nextBoolean();
  }
}
