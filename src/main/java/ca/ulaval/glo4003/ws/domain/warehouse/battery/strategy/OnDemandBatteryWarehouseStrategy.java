package ca.ulaval.glo4003.ws.domain.warehouse.battery.strategy;

import ca.ulaval.glo4003.ws.domain.manufacturer.battery.BatteryAssembledObserver;
import ca.ulaval.glo4003.ws.domain.manufacturer.battery.BatteryManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryInventoryObservable;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnDemandBatteryWarehouseStrategy extends BatteryInventoryObservable
    implements BatteryWarehouseStrategy, BatteryAssembledObserver {

  private static final Logger LOGGER = LogManager.getLogger();

  private final BatteryManufacturer batteryManufacturer;
  private final List<Order> ordersQueue = new ArrayList<>();

  public OnDemandBatteryWarehouseStrategy(BatteryManufacturer batteryManufacturer) {
    this.batteryManufacturer = batteryManufacturer;
  }

  @Override
  public void addOrder(Order order) {
    LOGGER.info(String.format("Battery order received: %s", order.getId()));
    batteryManufacturer.addOrder(order.getBatteryOrder());
    ordersQueue.add(order);

    AssemblyTime assemblyDelay = computeRemainingTimeToProduce(order.getId());

    if (!assemblyDelay.isOver()) {
      order.addAssemblyDelay(assemblyDelay);
      notifyBatteryDelay(order);
    }
  }

  @Override
  public AssemblyTime computeRemainingTimeToProduce(OrderId orderId) {
    Order order = findOrder(orderId);
    AssemblyTime delay = new AssemblyTime(0);
    if (ordersQueue.get(0) == order) {
      return delay;
    }
    return computeDelay(order);
  }

  @Override
  public List<Order> cancelAllOrders() {
    List<Order> activeOrders = new ArrayList<>(ordersQueue);
    ordersQueue.clear();
    return activeOrders;
  }

  @Override
  public void listenToBatteryAssembled(BatteryOrder batteryOrder) {
    Order order = findFirstOrderWaitingForBatteryType(batteryOrder.getBatteryType());
    notifyBatteryInStock(order);
    ordersQueue.remove(order);
  }

  private boolean isOrderFirstWaitingForBatteryType(OrderId orderId, String requiredBatteryType) {
    Order firstOrderWaitingForRequiredBatteryType =
        findFirstOrderWaitingForBatteryType(requiredBatteryType);
    return firstOrderWaitingForRequiredBatteryType.getId().equals(orderId);
  }

  private Order findFirstOrderWaitingForBatteryType(String batteryType) {
    return ordersQueue.stream()
        .filter(order -> order.getBatteryOrder().getBatteryType().equals(batteryType))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No order waiting for " + batteryType));
  }

  private Order findOrder(OrderId orderId) {
    return ordersQueue.stream()
        .filter(order -> order.getId().equals(orderId))
        .findFirst()
        .orElseThrow(
            () -> new RuntimeException("Order does not exist or battery already assembled"));
  }

  private AssemblyTime computeAssemblyTimeBeforeReceivingBatteryForOrder(Order order) {
    AssemblyTime timeBeforeReceivingBatteryTypeForOrder = new AssemblyTime(0);
    for (Order queuedOrder : ordersQueue) {
      timeBeforeReceivingBatteryTypeForOrder =
          timeBeforeReceivingBatteryTypeForOrder.add(
              queuedOrder.getBatteryOrder().getAssemblyTime());
      if (queuedOrder.getId().equals(order.getId())) {
        break;
      }
    }
    return timeBeforeReceivingBatteryTypeForOrder;
  }

  private AssemblyTime computeDelay(Order order) {
    AssemblyTime remainingTimeForNextBatteryOrderToBeAssembled =
        batteryManufacturer.computeRemainingTimeToProduceNextBatteryType(
            order.getBatteryOrder().getBatteryType());
    if (isOrderFirstWaitingForBatteryType(
        order.getId(), order.getBatteryOrder().getBatteryType())) {
      return remainingTimeForNextBatteryOrderToBeAssembled;
    }
    AssemblyTime timeBeforeReceivingBatteryTypeForOrder =
        computeAssemblyTimeBeforeReceivingBatteryForOrder(order);
    return timeBeforeReceivingBatteryTypeForOrder.subtract(
        remainingTimeForNextBatteryOrderToBeAssembled);
  }
}
