package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.evulution.car_manufacture.BasicVehicleAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.battery.LinearBatteryAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderQueue;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategyFactory;
import ca.ulaval.glo4003.ws.domain.vehicle.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.ModelRepository;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;
import ca.ulaval.glo4003.ws.infrastructure.assembly.model.LinearModelAssemblyLineStrategy;
import java.util.HashMap;
import java.util.Map;

public class AssemblyContext implements Context {
  public static ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerServices();
  }

  private void registerServices() {
    VehicleAssemblyLine vehicleAssemblyLine = new BasicVehicleAssemblyLine();
    Map<String, Integer> vehicleAssemblyConfiguration = createVehicleAssemblyConfiguration();
    vehicleAssemblyLine.configureAssemblyLine(vehicleAssemblyConfiguration);
    ModelAssemblyLineStrategy modelAssemblyLine =
        new LinearModelAssemblyLineStrategy(
            vehicleAssemblyLine, new CommandIdFactory(), new OrderQueue());
    BatteryAssemblyLineStrategy batteryAssemblyLine = new LinearBatteryAssemblyLine();

    AssemblyStrategyFactory assemblyStrategyFactory =
        new AssemblyStrategyFactory(modelAssemblyLine, batteryAssemblyLine);
    serviceLocator.register(
        AssemblyLine.class, new AssemblyLine(assemblyStrategyFactory, new OrderFactory()));
  }

  private Map<String, Integer> createVehicleAssemblyConfiguration() {
    ModelRepository modelRepository = serviceLocator.resolve(ModelRepository.class);
    Map<String, Integer> vehicleAssemblyConfiguration = new HashMap<>();
    for (Model model : modelRepository.findAllModels()) {
      vehicleAssemblyConfiguration.put(model.getName(), Integer.valueOf(model.getTimeToProduce()));
    }
    return vehicleAssemblyConfiguration;
  }
}
