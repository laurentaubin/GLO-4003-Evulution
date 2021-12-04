package ca.ulaval.glo4003.ws.service;

import ca.ulaval.glo4003.ws.domain.assembly.TransactionObserver;
import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AssemblyLineService implements TransactionObserver {
  private static final Logger LOGGER = LogManager.getLogger();

  private final AssemblyStrategy assemblyStrategy;
  private final OrderFactory orderFactory;
  private final ModelOrderFactory modelOrderFactory;
  private final BatteryOrderFactory batteryOrderFactory;

  public AssemblyLineService(
      OrderFactory orderFactory,
      AssemblyStrategy assemblyStrategy,
      ModelOrderFactory modelOrderFactory,
      BatteryOrderFactory batteryOrderFactory) {
    this.assemblyStrategy = assemblyStrategy;
    this.orderFactory = orderFactory;
    this.modelOrderFactory = modelOrderFactory;
    this.batteryOrderFactory = batteryOrderFactory;
  }

  public void advance() {
    LOGGER.info("Advancing time: +1 week");
    assemblyStrategy.advance();
  }

  @Override
  public void listenToTransactionCompleted(Transaction transaction) {
    ModelOrder modelOrder = this.createModelOrder(transaction.getVehicle().getModel());
    BatteryOrder batteryOrder = this.createBatteryOrder(transaction.getVehicle().getBattery());
    Order order = orderFactory.create(transaction.getId().toString(), modelOrder, batteryOrder);
    assemblyStrategy.addOrder(order);
  }

  public void activate() {
    assemblyStrategy.reactivate();
  }

  public void shutdown() {
    assemblyStrategy.shutdown();
  }

  public List<Order> getActiveOrders() {
    return assemblyStrategy.getActiveOrders();
  }

  private ModelOrder createModelOrder(Model model) {
    return modelOrderFactory.create(model.getName(), model.getProductionTime());
  }

  private BatteryOrder createBatteryOrder(Battery battery) {
    return batteryOrderFactory.create(battery.getType(), battery.getProductionTime());
  }
}
