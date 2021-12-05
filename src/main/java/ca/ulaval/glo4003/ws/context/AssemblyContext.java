package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.evulution.car_manufacture.BasicBatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BasicVehicleAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.battery.strategy.OnDemandBatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.model.strategy.AccumulateModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.model.strategy.JustInTimeModelAssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.model.strategy.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.model.strategy.OnDemandModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.LinearAssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTimeFactory;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.strategy.DefaultVehicleAssemblyLine;
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
import ca.ulaval.glo4003.ws.service.AssemblyLineService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AssemblyContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private static final String VEHICLE_PRODUCTION_LINE_MODE = "vehicleProductionLineMode";
  private static final List<String> MODEL_ASSEMBLY_ORDER_BY_NAME =
      List.of("Desjardins", "Vandry", "Pouliot");

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
    registerVehicleAssemblyObservers();
  }

  private void registerModelAssemblyLine() {
    VehicleAssemblyLine vehicleAssemblyLine = new BasicVehicleAssemblyLine();
    Map<String, Integer> vehicleAssemblyConfiguration = createVehicleAssemblyLineConfiguration();
    vehicleAssemblyLine.configureAssemblyLine(vehicleAssemblyConfiguration);
    ModelAssemblyLineAdapter modelAssemblyLineAdapter =
        new CarManufactureModelAssemblyLineAdapter(vehicleAssemblyLine, new CommandIdFactory());
    registerLinearModelAssemblyLineStrategy(modelAssemblyLineAdapter);
    registerAccumulateModelAssemblyLineStrategy(modelAssemblyLineAdapter);
    registerJustInTimeModelAssemblyLineStrategy(modelAssemblyLineAdapter);
  }

  private void registerBatteryAssemblyLine() {
    BatteryAssemblyLine basicBatteryAssemblyLine = new BasicBatteryAssemblyLine();
    basicBatteryAssemblyLine.configureAssemblyLine(createBatteryAssemblyLineConfiguration());
    BatteryAssemblyLineAdapter batteryAssemblyLineAdapter =
        new CarManufactureBatteryAssemblyLineAdapter(
            basicBatteryAssemblyLine, new CommandIdFactory());
    OnDemandBatteryAssemblyLineStrategy linearBatteryAssemblyLineStrategy =
        new OnDemandBatteryAssemblyLineStrategy(batteryAssemblyLineAdapter);
    linearBatteryAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
    serviceLocator.register(
        OnDemandBatteryAssemblyLineStrategy.class, linearBatteryAssemblyLineStrategy);
  }

  private void registerVehicleAssemblyLine() {
    VehicleAssemblyPlanner vehicleAssemblyPlanner =
        new VehicleAssemblyPlanner(new RandomProvider(new Random()));
    DefaultVehicleAssemblyLine defaultVehicleAssemblyLineStrategy =
        new DefaultVehicleAssemblyLine(vehicleAssemblyPlanner);
    serviceLocator.register(VehicleAssemblyPlanner.class, vehicleAssemblyPlanner);
    serviceLocator.register(DefaultVehicleAssemblyLine.class, defaultVehicleAssemblyLineStrategy);
  }

  private void registerAssemblyStrategy() {
    LinearAssemblyStrategy linearAssemblyStrategy =
        new LinearAssemblyStrategy(
            selectModelAssemblyLineStrategy(),
            serviceLocator.resolve(OnDemandBatteryAssemblyLineStrategy.class),
            serviceLocator.resolve(DefaultVehicleAssemblyLine.class),
            serviceLocator.resolve(OrderRepository.class));
    serviceLocator.register(LinearAssemblyStrategy.class, linearAssemblyStrategy);
  }

  private void registerAssemblyLine() {
    serviceLocator.register(
        AssemblyLineService.class,
        new AssemblyLineService(
            new OrderFactory(
                serviceLocator.resolve(LocalDateProvider.class),
                serviceLocator.resolve(VehicleAssemblyPlanner.class)),
            serviceLocator.resolve(LinearAssemblyStrategy.class),
            new ModelOrderFactory(),
            new BatteryOrderFactory(),
            new AssemblyTimeFactory()));
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
    OnDemandModelAssemblyLineStrategy modelAssemblyLineStrategy =
        serviceLocator.resolve(OnDemandModelAssemblyLineStrategy.class);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        serviceLocator.resolve(AccumulateModelAssemblyLineStrategy.class);
    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        serviceLocator.resolve(JustInTimeModelAssemblyStrategy.class);

    modelAssemblyLineStrategy.register(serviceLocator.resolve(LinearAssemblyStrategy.class));
    modelAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
    accumulateModelAssemblyLineStrategy.register(
        serviceLocator.resolve(LinearAssemblyStrategy.class));
    justInTimeModelAssemblyStrategy.register(serviceLocator.resolve(LinearAssemblyStrategy.class));
  }

  private void registerBatteryAssemblyObservers() {
    OnDemandBatteryAssemblyLineStrategy batteryAssemblyLineStrategy =
        serviceLocator.resolve(OnDemandBatteryAssemblyLineStrategy.class);
    batteryAssemblyLineStrategy.register(serviceLocator.resolve(LinearAssemblyStrategy.class));
    batteryAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
  }

  private void registerVehicleAssemblyObservers() {
    var vehicleAssemblyPlanner = serviceLocator.resolve(VehicleAssemblyPlanner.class);
    vehicleAssemblyPlanner.register(serviceLocator.resolve(NotificationService.class));
    DefaultVehicleAssemblyLine defaultVehicleAssemblyLine =
        serviceLocator.resolve(DefaultVehicleAssemblyLine.class);
    defaultVehicleAssemblyLine.register(serviceLocator.resolve(LinearAssemblyStrategy.class));
  }

  private void registerLinearModelAssemblyLineStrategy(
      ModelAssemblyLineAdapter modelAssemblyLineAdapter) {
    OnDemandModelAssemblyLineStrategy linearModelAssemblyLineStrategy =
        new OnDemandModelAssemblyLineStrategy(modelAssemblyLineAdapter);
    linearModelAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
    serviceLocator.register(
        OnDemandModelAssemblyLineStrategy.class, linearModelAssemblyLineStrategy);
  }

  private void registerAccumulateModelAssemblyLineStrategy(
      ModelAssemblyLineAdapter modelAssemblyLineAdapter) {
    List<ModelOrder> modelAssemblyOrder = createModelAssemblyOrder();
    ModelInventory modelInventory = new InMemoryModelInventory();
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        new AccumulateModelAssemblyLineStrategy(
            modelAssemblyOrder, modelAssemblyLineAdapter, modelInventory);
    serviceLocator.register(
        AccumulateModelAssemblyLineStrategy.class, accumulateModelAssemblyLineStrategy);
  }

  private void registerJustInTimeModelAssemblyLineStrategy(
      ModelAssemblyLineAdapter modelAssemblyLineAdapter) {
    List<ModelOrder> modelAssemblyOrder = createModelAssemblyOrder();
    ModelInventory modelInventory = new InMemoryModelInventory();
    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        new JustInTimeModelAssemblyStrategy(
            modelAssemblyLineAdapter, modelInventory, modelAssemblyOrder);
    serviceLocator.register(JustInTimeModelAssemblyStrategy.class, justInTimeModelAssemblyStrategy);
  }

  private List<ModelOrder> createModelAssemblyOrder() {
    ModelOrderFactory factory = new ModelOrderFactory();
    ModelRepository modelRepository = serviceLocator.resolve(ModelRepository.class);
    List<ModelOrder> modelAssemblyOrder = new ArrayList<>();
    for (String modelName : MODEL_ASSEMBLY_ORDER_BY_NAME) {
      Model model = modelRepository.findByModel(modelName);
      ModelOrder modelOrder =
          factory.create(model.getName(), new AssemblyTime(model.getProductionTime().inWeeks()));
      modelAssemblyOrder.add(modelOrder);
    }
    return modelAssemblyOrder;
  }

  private ModelAssemblyLineStrategy selectModelAssemblyLineStrategy() {
    String modelAssemblyLineMode = System.getProperty(VEHICLE_PRODUCTION_LINE_MODE);
    if (modelAssemblyLineMode == null || modelAssemblyLineMode.isEmpty()) {
      return serviceLocator.resolve(OnDemandModelAssemblyLineStrategy.class);
    } else if (modelAssemblyLineMode.equals("JIT")) {
      return serviceLocator.resolve(JustInTimeModelAssemblyStrategy.class);
    } else if (modelAssemblyLineMode.equals("CONTINUOUSLY")) {
      return serviceLocator.resolve(AccumulateModelAssemblyLineStrategy.class);
    } else {
      return serviceLocator.resolve(OnDemandModelAssemblyLineStrategy.class);
    }
  }
}
