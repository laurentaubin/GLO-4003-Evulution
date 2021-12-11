package ca.ulaval.glo4003.ws.domain.manufacturer.vehicle;

import ca.ulaval.glo4003.ws.domain.shared.RandomProvider;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class VehicleAssemblyPlanner {
  private static final Logger LOGGER = LogManager.getLogger();

  private final RandomProvider randomProvider;

  public VehicleAssemblyPlanner() {
    this(new RandomProvider(new Random()));
  }

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
      return VehicleAssemblyProductionTime.DELAYED.getAssemblyTime();
    }
    return VehicleAssemblyProductionTime.NORMAL.getAssemblyTime();
  }

  private boolean isDelayed() {
    return randomProvider.nextBoolean();
  }
}
