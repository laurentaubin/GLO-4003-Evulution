package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.battery.LinearBatteryAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.model.LinearModelAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategyFactory;

public class AssemblyContext implements Context {
  public static ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerServices();
  }

  private void registerServices() {
    ModelAssemblyLineStrategy modelAssemblyLine = new LinearModelAssemblyLine();
    BatteryAssemblyLineStrategy batteryAssemblyLine = new LinearBatteryAssemblyLine();

    AssemblyStrategyFactory assemblyStrategyFactory =
        new AssemblyStrategyFactory(modelAssemblyLine, batteryAssemblyLine);

    serviceLocator.register(
        AssemblyLine.class, new AssemblyLine(assemblyStrategyFactory, new OrderFactory()));
  }
}
