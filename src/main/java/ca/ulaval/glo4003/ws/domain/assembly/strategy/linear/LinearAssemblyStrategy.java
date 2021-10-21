package ca.ulaval.glo4003.ws.domain.assembly.strategy.linear;

import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.VehicleAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LinearAssemblyStrategy
    implements AssemblyStrategy, ModelAssembledObserver, BatteryAssembledObserver {
  private final ModelAssemblyLineStrategy modelAssemblyLineStrategy;
  private final BatteryAssemblyLineStrategy batteryAssemblyLineStrategy;
  private final VehicleAssemblyLineStrategy vehicleAssemblyLineStrategy;

  private static final Logger LOGGER = LogManager.getLogger();

  public LinearAssemblyStrategy(
      ModelAssemblyLineStrategy modelAssemblyLineStrategy,
      BatteryAssemblyLineStrategy batteryAssemblyLineStrategy,
      VehicleAssemblyLineStrategy vehicleAssemblyLineStrategy) {
    this.modelAssemblyLineStrategy = modelAssemblyLineStrategy;
    this.batteryAssemblyLineStrategy = batteryAssemblyLineStrategy;
    this.vehicleAssemblyLineStrategy = vehicleAssemblyLineStrategy;
  }

  @Override
  public void addOrder(Order order) {
    modelAssemblyLineStrategy.addOrder(order);
  }

  @Override
  public void advance() {
    LOGGER.info("Advancing time: +1 week");
    vehicleAssemblyLineStrategy.advance();
    batteryAssemblyLineStrategy.advance();
    modelAssemblyLineStrategy.advance();
  }

  @Override
  public void listenToBatteryAssembled(Order order) {
    vehicleAssemblyLineStrategy.assembleVehicle(order);
  }

  @Override
  public void listenToModelAssembled(Order order) {
    batteryAssemblyLineStrategy.addOrder(order);
  }
}
