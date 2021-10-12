package ca.ulaval.glo4003.ws.domain.assembly.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.linear.LinearAssemblyStrategy;

// TODO remove from exclude and write tests
public class AssemblyStrategyFactory {
  private final ModelAssemblyLineStrategy modelAssemblyLine;
  private final BatteryAssemblyLineStrategy batteryAssemblyLine;

  public AssemblyStrategyFactory(
      ModelAssemblyLineStrategy modelAssemblyLine,
      BatteryAssemblyLineStrategy batteryAssemblyLine) {
    this.modelAssemblyLine = modelAssemblyLine;
    this.batteryAssemblyLine = batteryAssemblyLine;
  }

  public AssemblyStrategy create(AssemblyStrategyType type) {
    switch (type) {
      case LINEAR:
        return new LinearAssemblyStrategy(modelAssemblyLine, batteryAssemblyLine);
    }
    // TODO: throw AssemblyStrategyNotImplemented/Registered
    throw new RuntimeException();
  }
}
