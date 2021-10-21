package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.evulution.car_manufacture.BasicBatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BasicVehicleAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.VehicleAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategyFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.linear.LinearAssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.linear.LinearBatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.linear.LinearModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.DefaultVehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.notification.NotificationService;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;
import ca.ulaval.glo4003.ws.infrastructure.assembly.battery.BatteryAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.infrastructure.assembly.model.ModelAssemblyLineAdapter;
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
    registerAssemblyStrategy();
    registerAssemblyLine();
    registerModelAssemblyObservers();
    registerBatteryAssemblyObservers();
  }

  private void registerModelAssemblyLine() {
    VehicleAssemblyLine vehicleAssemblyLine = new BasicVehicleAssemblyLine();
    Map<String, Integer> vehicleAssemblyConfiguration = createVehicleAssemblyLineConfiguration();
    vehicleAssemblyLine.configureAssemblyLine(vehicleAssemblyConfiguration);
    AssemblyLineAdapter modelAssemblyLineAdapter =
        new ModelAssemblyLineAdapter(vehicleAssemblyLine, new CommandIdFactory());
    LinearModelAssemblyLineStrategy linearModelAssemblyLineStrategy =
        new LinearModelAssemblyLineStrategy(modelAssemblyLineAdapter);
    linearModelAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
    serviceLocator.register(LinearModelAssemblyLineStrategy.class, linearModelAssemblyLineStrategy);
  }

  private void registerBatteryAssemblyLine() {
    BatteryAssemblyLine basicBatteryAssemblyLine = new BasicBatteryAssemblyLine();
    basicBatteryAssemblyLine.configureAssemblyLine(createBatteryAssemblyLineConfiguration());
    AssemblyLineAdapter batteryAssemblyLineAdapter =
        new BatteryAssemblyLineAdapter(basicBatteryAssemblyLine, new CommandIdFactory());
    LinearBatteryAssemblyLineStrategy linearBatteryAssemblyLineStrategy =
        new LinearBatteryAssemblyLineStrategy(batteryAssemblyLineAdapter);
    linearBatteryAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
    serviceLocator.register(
        LinearBatteryAssemblyLineStrategy.class, linearBatteryAssemblyLineStrategy);
  }

  private void registerVehicleAssemblyLine() {
    VehicleAssemblyPlanner vehicleAssemblyPlanner = new VehicleAssemblyPlanner(new Random());
    vehicleAssemblyPlanner.register(serviceLocator.resolve(NotificationService.class));
    VehicleAssemblyLineStrategy defaultVehicleAssemblyLineStrategy =
        new DefaultVehicleAssemblyLine(vehicleAssemblyPlanner);
    serviceLocator.register(VehicleAssemblyLineStrategy.class, defaultVehicleAssemblyLineStrategy);
  }

  private void registerAssemblyStrategy() {
    LinearAssemblyStrategy linearAssemblyStrategy =
        new LinearAssemblyStrategy(
            serviceLocator.resolve(LinearModelAssemblyLineStrategy.class),
            serviceLocator.resolve(LinearBatteryAssemblyLineStrategy.class),
            serviceLocator.resolve(VehicleAssemblyLineStrategy.class));
    serviceLocator.register(LinearAssemblyStrategy.class, linearAssemblyStrategy);
  }

  private void registerAssemblyLine() {
    AssemblyStrategyFactory assemblyStrategyFactory =
        new AssemblyStrategyFactory(
            serviceLocator.resolve(LinearModelAssemblyLineStrategy.class),
            serviceLocator.resolve(LinearBatteryAssemblyLineStrategy.class),
            serviceLocator.resolve(VehicleAssemblyLineStrategy.class));
    serviceLocator.register(
        AssemblyLine.class,
        new AssemblyLine(
            assemblyStrategyFactory,
            new OrderFactory(),
            serviceLocator.resolve(LinearAssemblyStrategy.class)));
  }

  private Map<String, Integer> createVehicleAssemblyLineConfiguration() {
    ModelRepository modelRepository = serviceLocator.resolve(ModelRepository.class);
    Map<String, Integer> vehicleAssemblyConfiguration = new HashMap<>();
    for (Model model : modelRepository.findAllModels()) {
      vehicleAssemblyConfiguration.put(model.getName(), model.getProductionTime().inWeeks());
    }
    return vehicleAssemblyConfiguration;
  }

  private Map<String, Integer> createBatteryAssemblyLineConfiguration() {
    BatteryRepository batteryRepository = serviceLocator.resolve(BatteryRepository.class);
    Map<String, Integer> batteryAssemblyLineConfiguration = new HashMap<>();
    for (Battery battery : batteryRepository.findAllBatteries()) {
      batteryAssemblyLineConfiguration.put(
          battery.getType(), battery.getProductionTime().inWeeks());
    }
    return batteryAssemblyLineConfiguration;
  }

  private void registerModelAssemblyObservers() {
    LinearModelAssemblyLineStrategy modelAssemblyLineStrategy =
        serviceLocator.resolve(LinearModelAssemblyLineStrategy.class);
    modelAssemblyLineStrategy.register(serviceLocator.resolve(LinearAssemblyStrategy.class));
  }

  private void registerBatteryAssemblyObservers() {
    LinearBatteryAssemblyLineStrategy batteryAssemblyLineStrategy =
        serviceLocator.resolve(LinearBatteryAssemblyLineStrategy.class);
    batteryAssemblyLineStrategy.register(serviceLocator.resolve(LinearAssemblyStrategy.class));
  }
}
