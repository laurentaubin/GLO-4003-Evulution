package ca.ulaval.glo4003.ws.service.warehouse;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.domain.warehouse.OrderDelayObservable;
import ca.ulaval.glo4003.ws.domain.warehouse.TransactionCompletedObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrderFactory;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrderFactory;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.warehouse.strategy.LinearWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.strategy.WarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTimeFactory;
import ca.ulaval.glo4003.ws.service.user.UserService;

import java.util.List;

public class WarehouseService extends OrderDelayObservable implements TransactionCompletedObserver {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final WarehouseStrategy warehouseStrategy;
  private final OrderFactory orderFactory;
  private final ModelOrderFactory modelOrderFactory;
  private final BatteryOrderFactory batteryOrderFactory;
  private final AssemblyTimeFactory assemblyTimeFactory;

  public WarehouseService() {
    this(
        serviceLocator.resolve(OrderFactory.class),
        serviceLocator.resolve(LinearWarehouseStrategy.class),
        new ModelOrderFactory(),
        new BatteryOrderFactory(),
        new AssemblyTimeFactory());
  }

  public WarehouseService(
      OrderFactory orderFactory,
      WarehouseStrategy warehouseStrategy,
      ModelOrderFactory modelOrderFactory,
      BatteryOrderFactory batteryOrderFactory,
      AssemblyTimeFactory assemblyTimeFactory) {
      this.warehouseStrategy = warehouseStrategy;
      this.orderFactory = orderFactory;
    this.modelOrderFactory = modelOrderFactory;
    this.batteryOrderFactory = batteryOrderFactory;
    this.assemblyTimeFactory = assemblyTimeFactory;
  }

  @Override
  public void listenToTransactionCompleted(Transaction transaction) {
    ModelOrder modelOrder = this.createModelOrder(transaction.getVehicle().getModel());
    BatteryOrder batteryOrder = this.createBatteryOrder(transaction.getVehicle().getBattery());
    Order order = orderFactory.create(transaction.getId().toString(), modelOrder, batteryOrder);
    warehouseStrategy.addOrder(order);
  }

  private ModelOrder createModelOrder(Model model) {
    return modelOrderFactory.create(
        model.getName(), assemblyTimeFactory.create(model.getProductionTime().inWeeks()));
  }

  private BatteryOrder createBatteryOrder(Battery battery) {
    return batteryOrderFactory.create(
        battery.getType(), assemblyTimeFactory.create(battery.getProductionTime().inWeeks()));
  }
}
