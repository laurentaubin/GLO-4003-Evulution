package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.evulution.car_manufacture.BasicBatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BasicVehicleAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.api.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.VehicleAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model.AccumulateModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model.ModelOrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.linear.LinearAssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.linear.LinearBatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.linear.LinearModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.DefaultVehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.notification.NotificationService;
import ca.ulaval.glo4003.ws.domain.shared.RandomProvider;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;
import ca.ulaval.glo4003.ws.infrastructure.assembly.battery.CarManufactureBatteryAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.infrastructure.assembly.model.CarManufactureModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.infrastructure.assembly.model.InMemoryModelInventory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AssemblyContext implements Context {
  private static final boolean ACCUMULATE_MODEL_ASSEMBLY_LINE_ENABELD = true;
  private static final List<String> MODEL_ASSEMBLY_ORDER_BY_NAME =
      List.of("Desjardins", "Vandry", "Pouliot");

  public static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

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
    ModelAssemblyLineAdapter modelAssemblyLineAdapter =
        new CarManufactureModelAssemblyLineAdapter(vehicleAssemblyLine, new CommandIdFactory());
    registerLinearModelAssemblyLineStrategy(modelAssemblyLineAdapter);
    registerAccumulateModelAssemblyLineStrategy(modelAssemblyLineAdapter);
  }

  private void registerBatteryAssemblyLine() {
    BatteryAssemblyLine basicBatteryAssemblyLine = new BasicBatteryAssemblyLine();
    basicBatteryAssemblyLine.configureAssemblyLine(createBatteryAssemblyLineConfiguration());
    BatteryAssemblyLineAdapter batteryAssemblyLineAdapter =
        new CarManufactureBatteryAssemblyLineAdapter(
            basicBatteryAssemblyLine, new CommandIdFactory());
    LinearBatteryAssemblyLineStrategy linearBatteryAssemblyLineStrategy =
        new LinearBatteryAssemblyLineStrategy(batteryAssemblyLineAdapter);
    linearBatteryAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
    serviceLocator.register(
        LinearBatteryAssemblyLineStrategy.class, linearBatteryAssemblyLineStrategy);
  }

  private void registerVehicleAssemblyLine() {
    VehicleAssemblyPlanner vehicleAssemblyPlanner =
        new VehicleAssemblyPlanner(new RandomProvider(new Random()));
    vehicleAssemblyPlanner.register(serviceLocator.resolve(NotificationService.class));
    VehicleAssemblyLineStrategy defaultVehicleAssemblyLineStrategy =
        new DefaultVehicleAssemblyLine(vehicleAssemblyPlanner);
    serviceLocator.register(VehicleAssemblyPlanner.class, vehicleAssemblyPlanner);
    serviceLocator.register(VehicleAssemblyLineStrategy.class, defaultVehicleAssemblyLineStrategy);
  }

  private void registerAssemblyStrategy() {
    LinearAssemblyStrategy linearAssemblyStrategy =
        new LinearAssemblyStrategy(
            serviceLocator.resolve(AccumulateModelAssemblyLineStrategy.class),
            serviceLocator.resolve(LinearBatteryAssemblyLineStrategy.class),
            serviceLocator.resolve(VehicleAssemblyLineStrategy.class));
    serviceLocator.register(LinearAssemblyStrategy.class, linearAssemblyStrategy);
  }

  private void registerAssemblyLine() {
    serviceLocator.register(
        AssemblyLine.class,
        new AssemblyLine(
            new OrderFactory(
                new LocalDateProvider(), serviceLocator.resolve(VehicleAssemblyPlanner.class)),
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
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        serviceLocator.resolve(AccumulateModelAssemblyLineStrategy.class);
    modelAssemblyLineStrategy.register(serviceLocator.resolve(LinearAssemblyStrategy.class));
    accumulateModelAssemblyLineStrategy.register(
        serviceLocator.resolve(LinearAssemblyStrategy.class));
  }

  private void registerBatteryAssemblyObservers() {
    LinearBatteryAssemblyLineStrategy batteryAssemblyLineStrategy =
        serviceLocator.resolve(LinearBatteryAssemblyLineStrategy.class);
    batteryAssemblyLineStrategy.register(serviceLocator.resolve(LinearAssemblyStrategy.class));
  }

  private void registerLinearModelAssemblyLineStrategy(
      ModelAssemblyLineAdapter modelAssemblyLineAdapter) {
    LinearModelAssemblyLineStrategy linearModelAssemblyLineStrategy =
        new LinearModelAssemblyLineStrategy(modelAssemblyLineAdapter);
    linearModelAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
    serviceLocator.register(LinearModelAssemblyLineStrategy.class, linearModelAssemblyLineStrategy);
  }

  private void registerAccumulateModelAssemblyLineStrategy(
      ModelAssemblyLineAdapter modelAssemblyLineAdapter) {
    List<Model> modelAssemblyOrder = createModelAssemblyOrder();
    ModelInventory modelInventory = new InMemoryModelInventory();
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        new AccumulateModelAssemblyLineStrategy(
            modelAssemblyOrder, modelAssemblyLineAdapter, modelInventory, new ModelOrderFactory());
    serviceLocator.register(
        AccumulateModelAssemblyLineStrategy.class, accumulateModelAssemblyLineStrategy);
  }

  public List<Model> createModelAssemblyOrder() {
    ModelRepository modelRepository = serviceLocator.resolve(ModelRepository.class);
    List<Model> modelAssemblyOrder = new ArrayList<>();
    for (String modelName : MODEL_ASSEMBLY_ORDER_BY_NAME) {
      modelAssemblyOrder.add(modelRepository.findByModel(modelName));
    }
    return modelAssemblyOrder;
  }
}
