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
import ca.ulaval.glo4003.ws.domain.notification.NotificationService;
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
    registerModelAssemblyLine();
    registerBatteryAssemblyLine();
    registerVehicleAssemblyLine();
  }

  private void registerModelAssemblyLine() {
    VehicleAssemblyLine vehicleAssemblyLine = new BasicVehicleAssemblyLine();
    Map<String, Integer> vehicleAssemblyConfiguration = createVehicleAssemblyConfiguration();
    vehicleAssemblyLine.configureAssemblyLine(vehicleAssemblyConfiguration);
    LinearModelAssemblyLineStrategy linearModelAssemblyLineStrategy =
        new LinearModelAssemblyLineStrategy(vehicleAssemblyLine, new CommandIdFactory());
    linearModelAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
    serviceLocator.register(ModelAssemblyLineStrategy.class, linearModelAssemblyLineStrategy);
  }

  private void registerBatteryAssemblyLine() {
    BatteryAssemblyLine basicBatteryAssemblyLine = new BasicBatteryAssemblyLine();
    LinearBatteryAssemblyLineStrategy linearBatteryAssemblyLineStrategy =
        new LinearBatteryAssemblyLineStrategy(basicBatteryAssemblyLine, new CommandIdFactory());
    linearBatteryAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
    serviceLocator.register(BatteryAssemblyLineStrategy.class, linearBatteryAssemblyLineStrategy);
  }

  private void registerVehicleAssemblyLine() {
    VehicleAssemblyPlanner vehicleAssemblyPlanner = new VehicleAssemblyPlanner(new Random());
    vehicleAssemblyPlanner.register(serviceLocator.resolve(NotificationService.class));
    VehicleAssemblyLineStrategy defaultVehicleAssemblyLine =
        new DefaultVehicleAssemblyLine(vehicleAssemblyPlanner);
    AssemblyStrategyFactory assemblyStrategyFactory =
        new AssemblyStrategyFactory(
            serviceLocator.resolve(ModelAssemblyLineStrategy.class),
            serviceLocator.resolve(BatteryAssemblyLineStrategy.class),
            defaultVehicleAssemblyLine);
    serviceLocator.register(
        AssemblyLine.class, new AssemblyLine(assemblyStrategyFactory, new OrderFactory()));
  }

  private Map<String, Integer> createVehicleAssemblyConfiguration() {
    ModelRepository modelRepository = serviceLocator.resolve(ModelRepository.class);
    Map<String, Integer> vehicleAssemblyConfiguration = new HashMap<>();
    for (Model model : modelRepository.findAllModels()) {
      vehicleAssemblyConfiguration.put(model.getName(), model.getTimeToProduce());
    }
    return vehicleAssemblyConfiguration;
  }
}
