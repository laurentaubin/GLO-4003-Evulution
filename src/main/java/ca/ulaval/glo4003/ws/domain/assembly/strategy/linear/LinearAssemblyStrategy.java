package ca.ulaval.glo4003.ws.domain.assembly.strategy.linear;

import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;

// TODO remove from exclude and write tests
public class LinearAssemblyStrategy
    implements AssemblyStrategy, ModelAssembledObserver, BatteryAssembledObserver {
  private final ModelAssemblyLineStrategy modelAssemblyLineStrategy;
  private final BatteryAssemblyLineStrategy batteryAssemblyLineStrategy;

  public LinearAssemblyStrategy(
      ModelAssemblyLineStrategy modelAssemblyLineStrategy,
      BatteryAssemblyLineStrategy batteryAssemblyLineStrategy) {
    this.modelAssemblyLineStrategy = modelAssemblyLineStrategy;
    this.batteryAssemblyLineStrategy = batteryAssemblyLineStrategy;
  }

  @Override
  public void addOrder(Order order) {
    modelAssemblyLineStrategy.addOrder(order);
  }

  @Override
  public void advance() {
    //    modelAssemblyLine.advance();
    //    batteryAssemblyLine.advance();
  }

  @Override
  public void listenToBatteryAssembled() {}

  @Override
  public void listenToModelAssembled(Order order) {
    batteryAssemblyLineStrategy.assembleBattery(order);
  }
}
