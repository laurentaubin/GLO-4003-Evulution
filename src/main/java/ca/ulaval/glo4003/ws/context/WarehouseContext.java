package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.evulution.car_manufacture.BasicBatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BasicVehicleAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.manufacturer.battery.BatteryAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.manufacturer.battery.BatteryManufacturerImpl;
import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelManufacturerImpl;
import ca.ulaval.glo4003.ws.domain.manufacturer.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.manufacturer.vehicle.VehicleManufacturerImpl;
import ca.ulaval.glo4003.ws.domain.notification.NotificationService;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.strategy.OnDemandBatteryWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrderFactory;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.model.strategy.AccumulateModelWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.model.strategy.JustInTimeModelWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.model.strategy.OnDemandModelWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.warehouse.strategy.LinearWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.domain.warehouse.vehicle.strategy.DefaultVehicleWarehouseStrategy;
import ca.ulaval.glo4003.ws.infrastructure.manufacturer.battery.BatteryAssemblyLineAdapterImpl;
import ca.ulaval.glo4003.ws.infrastructure.manufacturer.model.InMemoryModelInventory;
import ca.ulaval.glo4003.ws.infrastructure.manufacturer.model.ModelAssemblyLineAdapterImpl;
import ca.ulaval.glo4003.ws.service.warehouse.WarehouseService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private static final String VEHICLE_PRODUCTION_LINE_MODE = "vehicleProductionLineMode";
  private static final List<String> MODEL_ASSEMBLY_ORDER_BY_NAME =
      List.of("Desjardins", "Vandry", "Pouliot");

  @Override
  public void registerContext() {
    registerServices();
  }

  private void registerServices() {
    serviceLocator.register(VehicleAssemblyPlanner.class, new VehicleAssemblyPlanner());

    BatteryManufacturerImpl batteryManufacturer = createBatteryManufacturer();
    ModelManufacturerImpl modelManufacturer = createModelManufacturer();
    VehicleManufacturerImpl vehicleManufacturer = new VehicleManufacturerImpl();
    serviceLocator.register(ModelManufacturerImpl.class, modelManufacturer);
    serviceLocator.register(BatteryManufacturerImpl.class, batteryManufacturer);
    serviceLocator.register(VehicleManufacturerImpl.class, vehicleManufacturer);

    registerModelInventory();
    registerModelWarehouseStrategy(modelManufacturer);
    registerBatteryWarehouseStrategy(batteryManufacturer);
    registerVehicleWarehouseStrategy(vehicleManufacturer);
    registerWarehouseStrategy(modelManufacturer);
    registerWarehouse();
    registerModelAssembledObservers();
    registerBatteryAssembledObservers();
    registerVehicleAssembledObservers();

    serviceLocator
        .resolve(WarehouseService.class)
        .register(serviceLocator.resolve(NotificationService.class));
  }

  private BatteryManufacturerImpl createBatteryManufacturer() {
    Map<String, Integer> batteryAssemblyLineConfiguration =
        createBatteryAssemblyLineConfiguration();
    BatteryAssemblyLine batteryAssemblyLine = new BasicBatteryAssemblyLine();
    batteryAssemblyLine.configureAssemblyLine(batteryAssemblyLineConfiguration);

    serviceLocator.register(BatteryAssemblyLine.class, batteryAssemblyLine);
    serviceLocator.register(BatteryAssemblyLineAdapter.class, new BatteryAssemblyLineAdapterImpl());

    return new BatteryManufacturerImpl();
  }

  private ModelManufacturerImpl createModelManufacturer() {
    Map<String, Integer> vehicleAssemblyLineConfiguration =
        createVehicleAssemblyLineConfiguration();
    VehicleAssemblyLine vehicleAssemblyLine = new BasicVehicleAssemblyLine();
    vehicleAssemblyLine.configureAssemblyLine(vehicleAssemblyLineConfiguration);

    serviceLocator.register(VehicleAssemblyLine.class, vehicleAssemblyLine);
    serviceLocator.register(ModelAssemblyLineAdapter.class, new ModelAssemblyLineAdapterImpl());

    return new ModelManufacturerImpl();
  }

  private void registerModelWarehouseStrategy(ModelManufacturerImpl modelManufacturer) {
    String modelAssemblyLineMode = System.getProperty(VEHICLE_PRODUCTION_LINE_MODE);
    if (modelAssemblyLineMode == null || modelAssemblyLineMode.isEmpty()) {
      registerOnDemandModelWarehouseStrategy(modelManufacturer);
    } else if (modelAssemblyLineMode.equals("JIT")) {
      registerJustInTimeModelAssemblyLineStrategy(modelManufacturer);
    } else if (modelAssemblyLineMode.equals("CONTINUOUSLY")) {
      registerAccumulateModelAssemblyLineStrategy(modelManufacturer);
    } else {
      registerOnDemandModelWarehouseStrategy(modelManufacturer);
    }
  }

  private void registerBatteryWarehouseStrategy(BatteryManufacturerImpl batteryManufacturer) {
    BatteryAssemblyLine batteryAssemblyLine = new BasicBatteryAssemblyLine();
    batteryAssemblyLine.configureAssemblyLine(createBatteryAssemblyLineConfiguration());
    OnDemandBatteryWarehouseStrategy onDemandBatteryWarehouseStrategy =
        new OnDemandBatteryWarehouseStrategy(batteryManufacturer);
    batteryManufacturer.register(onDemandBatteryWarehouseStrategy);
    onDemandBatteryWarehouseStrategy.register(serviceLocator.resolve(NotificationService.class));
    serviceLocator.register(
        OnDemandBatteryWarehouseStrategy.class, onDemandBatteryWarehouseStrategy);
  }

  private void registerVehicleWarehouseStrategy(VehicleManufacturerImpl vehicleManufacturer) {
    DefaultVehicleWarehouseStrategy defaultVehicleAssemblyLineStrategy =
        new DefaultVehicleWarehouseStrategy(vehicleManufacturer);
    vehicleManufacturer.register(defaultVehicleAssemblyLineStrategy);
    serviceLocator.register(
        DefaultVehicleWarehouseStrategy.class, defaultVehicleAssemblyLineStrategy);
  }

  private void registerWarehouseStrategy(ModelManufacturerImpl modelManufacturer) {
    LinearWarehouseStrategy linearWarehouseStrategy =
        new LinearWarehouseStrategy(
            selectModelAssemblyLineStrategy(modelManufacturer),
            serviceLocator.resolve(OnDemandBatteryWarehouseStrategy.class),
            serviceLocator.resolve(DefaultVehicleWarehouseStrategy.class),
            serviceLocator.resolve(OrderRepository.class));
    serviceLocator.register(LinearWarehouseStrategy.class, linearWarehouseStrategy);
  }

  private void registerWarehouse() {
    serviceLocator.register(OrderFactory.class, new OrderFactory());
    serviceLocator.register(WarehouseService.class, new WarehouseService());
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

  private void registerModelAssembledObservers() {
    String modelAssemblyLineMode = System.getProperty(VEHICLE_PRODUCTION_LINE_MODE);
    if (modelAssemblyLineMode == null || modelAssemblyLineMode.isEmpty()) {
      OnDemandModelWarehouseStrategy onDemandModelWarehouseStrategy =
          serviceLocator.resolve(OnDemandModelWarehouseStrategy.class);
      onDemandModelWarehouseStrategy.register(
          serviceLocator.resolve(LinearWarehouseStrategy.class));
      onDemandModelWarehouseStrategy.register(serviceLocator.resolve(NotificationService.class));
    } else if (modelAssemblyLineMode.equals("JIT")) {
      JustInTimeModelWarehouseStrategy justInTimeModelAssemblyLineStrategy =
          serviceLocator.resolve(JustInTimeModelWarehouseStrategy.class);
      justInTimeModelAssemblyLineStrategy.register(
          serviceLocator.resolve(LinearWarehouseStrategy.class));
      justInTimeModelAssemblyLineStrategy.register(
          serviceLocator.resolve(NotificationService.class));
    } else if (modelAssemblyLineMode.equals("CONTINUOUSLY")) {
      AccumulateModelWarehouseStrategy accumulateModelAssemblyLineStrategy =
          serviceLocator.resolve(AccumulateModelWarehouseStrategy.class);
      accumulateModelAssemblyLineStrategy.register(
          serviceLocator.resolve(LinearWarehouseStrategy.class));
      accumulateModelAssemblyLineStrategy.register(
          serviceLocator.resolve(NotificationService.class));
    } else {
      OnDemandModelWarehouseStrategy onDemandModelWarehouseStrategy =
          serviceLocator.resolve(OnDemandModelWarehouseStrategy.class);
      onDemandModelWarehouseStrategy.register(
          serviceLocator.resolve(LinearWarehouseStrategy.class));
      onDemandModelWarehouseStrategy.register(serviceLocator.resolve(NotificationService.class));
    }
  }

  private void registerBatteryAssembledObservers() {
    OnDemandBatteryWarehouseStrategy batteryAssemblyLineStrategy =
        serviceLocator.resolve(OnDemandBatteryWarehouseStrategy.class);
    batteryAssemblyLineStrategy.register(serviceLocator.resolve(LinearWarehouseStrategy.class));
    batteryAssemblyLineStrategy.register(serviceLocator.resolve(NotificationService.class));
  }

  private void registerVehicleAssembledObservers() {
    VehicleManufacturerImpl vehicleManufacturer =
        serviceLocator.resolve(VehicleManufacturerImpl.class);
    vehicleManufacturer.register(serviceLocator.resolve(NotificationService.class));
    DefaultVehicleWarehouseStrategy defaultVehicleAssemblyLine =
        serviceLocator.resolve(DefaultVehicleWarehouseStrategy.class);
    defaultVehicleAssemblyLine.register(serviceLocator.resolve(LinearWarehouseStrategy.class));
  }

  private void registerOnDemandModelWarehouseStrategy(ModelManufacturerImpl modelManufacturer) {
    OnDemandModelWarehouseStrategy onDemandModelWarehouseStrategy =
        new OnDemandModelWarehouseStrategy(modelManufacturer);
    onDemandModelWarehouseStrategy.register(serviceLocator.resolve(NotificationService.class));
    serviceLocator.register(OnDemandModelWarehouseStrategy.class, onDemandModelWarehouseStrategy);
  }

  private void registerAccumulateModelAssemblyLineStrategy(
      ModelManufacturerImpl modelManufacturer) {
    List<ModelOrder> modelAssemblyOrder = createModelAssemblyOrder();
    AccumulateModelWarehouseStrategy accumulateModelAssemblyLineStrategy =
        new AccumulateModelWarehouseStrategy(modelAssemblyOrder, modelManufacturer);
    serviceLocator.register(
        AccumulateModelWarehouseStrategy.class, accumulateModelAssemblyLineStrategy);
  }

  private void registerJustInTimeModelAssemblyLineStrategy(
      ModelManufacturerImpl modelManufacturer) {
    List<ModelOrder> modelAssemblyOrder = createModelAssemblyOrder();
    JustInTimeModelWarehouseStrategy justInTimeModelAssemblyLineStrategy =
        new JustInTimeModelWarehouseStrategy(modelManufacturer, modelAssemblyOrder);
    serviceLocator.register(
        JustInTimeModelWarehouseStrategy.class, justInTimeModelAssemblyLineStrategy);
  }

  private void registerModelInventory() {
    serviceLocator.register(ModelInventory.class, new InMemoryModelInventory());
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

  private ModelWarehouseStrategy selectModelAssemblyLineStrategy(
      ModelManufacturerImpl modelManufacturer) {
    String modelAssemblyLineMode = System.getProperty(VEHICLE_PRODUCTION_LINE_MODE);
    if (modelAssemblyLineMode == null || modelAssemblyLineMode.isEmpty()) {
      return selectOnDemandModelWarehouseStrategy(modelManufacturer);
    } else if (modelAssemblyLineMode.equals("JIT")) {
      return selectJustInTimeModelWarehouseStrategy(modelManufacturer);
    } else if (modelAssemblyLineMode.equals("CONTINUOUSLY")) {
      return selectAccumulateModelWarehouseStrategy(modelManufacturer);
    } else {
      return selectOnDemandModelWarehouseStrategy(modelManufacturer);
    }
  }

  private OnDemandModelWarehouseStrategy selectOnDemandModelWarehouseStrategy(
      ModelManufacturerImpl modelManufacturer) {
    OnDemandModelWarehouseStrategy onDemandModelWarehouseStrategy =
        serviceLocator.resolve(OnDemandModelWarehouseStrategy.class);
    modelManufacturer.register(onDemandModelWarehouseStrategy);
    return onDemandModelWarehouseStrategy;
  }

  private JustInTimeModelWarehouseStrategy selectJustInTimeModelWarehouseStrategy(
      ModelManufacturerImpl modelManufacturer) {
    JustInTimeModelWarehouseStrategy justInTimeModelWarehouseStrategy =
        serviceLocator.resolve(JustInTimeModelWarehouseStrategy.class);
    modelManufacturer.register(justInTimeModelWarehouseStrategy);
    return justInTimeModelWarehouseStrategy;
  }

  private AccumulateModelWarehouseStrategy selectAccumulateModelWarehouseStrategy(
      ModelManufacturerImpl modelManufacturer) {
    AccumulateModelWarehouseStrategy accumulateModelWarehouseStrategy =
        serviceLocator.resolve(AccumulateModelWarehouseStrategy.class);
    modelManufacturer.register(accumulateModelWarehouseStrategy);
    return accumulateModelWarehouseStrategy;
  }
}
