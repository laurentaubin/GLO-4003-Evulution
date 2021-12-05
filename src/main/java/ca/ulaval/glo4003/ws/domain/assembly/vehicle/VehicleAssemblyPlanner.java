package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import ca.ulaval.glo4003.ws.domain.shared.RandomProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VehicleAssemblyPlanner extends VehicleAssemblyObservable {
  private static final Logger LOGGER = LogManager.getLogger();

  private final RandomProvider randomProvider;

  public VehicleAssemblyPlanner(RandomProvider randomProvider) {
    this.randomProvider = randomProvider;
  }

  public AssemblyTime getNormalAssemblyTime() {
    return VehicleAssemblyProductionTime.NORMAL.getAssemblyTime();
  }

  public AssemblyTime getAssemblyTime(Order order) {
    if (isDelayed()) {
      LOGGER.info(String.format("Random delay for order %s", order.getId()));
      AssemblyTime assemblyDelay =
          VehicleAssemblyProductionTime.DELAYED
              .getAssemblyTime()
              .subtract(VehicleAssemblyProductionTime.NORMAL.getAssemblyTime());
      order.addAssemblyDelay(assemblyDelay);
      notifyVehicleAssemblyDelay(order);
      return VehicleAssemblyProductionTime.DELAYED.getAssemblyTime();
    }
    return VehicleAssemblyProductionTime.NORMAL.getAssemblyTime();
  }

  private boolean isDelayed() {
    return randomProvider.nextBoolean();
  }
}
