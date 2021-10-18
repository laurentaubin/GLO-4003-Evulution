package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.evulution.car_manufacture.BasicBatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BasicVehicleAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.VehicleAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategyFactory;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.DefaultVehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.vehicle.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.ModelRepository;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;
import ca.ulaval.glo4003.ws.infrastructure.assembly.battery.LinearBatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.infrastructure.assembly.model.LinearModelAssemblyLineStrategy;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        new LinearModelAssemblyLineStrategy(vehicleAssemblyLine, new CommandIdFactory());

    BatteryAssemblyLine basicBatteryAssemblyLine = new BasicBatteryAssemblyLine();
    BatteryAssemblyLineStrategy batteryAssemblyLine =
        new LinearBatteryAssemblyLineStrategy(basicBatteryAssemblyLine, new CommandIdFactory());

    VehicleAssemblyLineStrategy defaultVehicleAssemblyLine =
        new DefaultVehicleAssemblyLine(new VehicleAssemblyPlanner(new Random()));
    AssemblyStrategyFactory assemblyStrategyFactory =
        new AssemblyStrategyFactory(
            modelAssemblyLine, batteryAssemblyLine, defaultVehicleAssemblyLine);
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
