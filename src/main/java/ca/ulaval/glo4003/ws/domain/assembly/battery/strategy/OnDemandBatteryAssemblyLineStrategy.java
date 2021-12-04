package ca.ulaval.glo4003.ws.domain.assembly.battery.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryAssemblyObservable;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnDemandBatteryAssemblyLineStrategy extends BatteryAssemblyObservable
    implements BatteryAssemblyLineStrategy {

  private static final Logger LOGGER = LogManager.getLogger();

  private final BatteryAssemblyLineAdapter batteryAssemblyLineAdapter;
  private final Queue<Order> orderQueue = new LinkedList<>();

  private Order currentOrder;
  private ProductionTime currentOrderRemainingTimeToProduce;

  public OnDemandBatteryAssemblyLineStrategy(
      BatteryAssemblyLineAdapter batteryAssemblyLineAdapter) {
    this.batteryAssemblyLineAdapter = batteryAssemblyLineAdapter;
  }

  @Override
  public void advance() {
    batteryAssemblyLineAdapter.advance();
    if (isAnOrderBeingAssembled()) {
      processCurrentOrder();
    }
    if (isNextOrderReadyToBeAssembled()) {
      sendNextOrderToBeAssembled();
    }
  }

  @Override
  public void addOrder(Order order) {
    LOGGER.info(String.format("Battery order received: %s", order.getId()));
    if (orderQueue.isEmpty() && isAssemblyLineFree()) {
      sendOrderToBeAssembled(order);
      return;
    }
    orderQueue.add(order);

    ProductionTime assemblyDelay = computeAssemblyDelay(order);
    if (!assemblyDelay.isOver()) {
      order.addAssemblyDelay(assemblyDelay);
      notifyBatteryAssemblyDelay(order);
    }
  }

  @Override
  public ProductionTime computeRemainingTimeToProduce(OrderId orderId) {
    if (orderId == currentOrder.getId()) {
      return currentOrderRemainingTimeToProduce;
    }
    return computeRemainingTimeToProduceBasedOnPositionInQueue(orderId);
  }

  @Override
  public List<Order> getActiveOrders() {
    List<Order> activeOrders = new ArrayList<>(orderQueue);
    if (isAnOrderBeingAssembled()) {
      activeOrders.add(0, currentOrder);
    }
    return activeOrders;
  }

  @Override
  public void shutdown() {
    currentOrder = null;
    currentOrderRemainingTimeToProduce = null;
    orderQueue.clear();
  }

  private boolean isAssemblyLineFree() {
    if (currentOrder == null) {
      return true;
    }
    return batteryAssemblyLineAdapter
        .getAssemblyStatus(currentOrder.getId())
        .equals(AssemblyStatus.ASSEMBLED);
  }

  private boolean isAnOrderBeingAssembled() {
    return this.currentOrder != null;
  }

  private boolean isNextOrderReadyToBeAssembled() {
    return isAssemblyLineFree() && !orderQueue.isEmpty();
  }

  private void sendOrderToBeAssembled(Order order) {
    batteryAssemblyLineAdapter.addOrder(order);
    currentOrder = order;
    currentOrderRemainingTimeToProduce = order.getBatteryOrder().getProductionTime();
  }

  private void sendNextOrderToBeAssembled() {
    currentOrder = orderQueue.remove();
    currentOrderRemainingTimeToProduce = currentOrder.getBatteryOrder().getProductionTime();
    batteryAssemblyLineAdapter.addOrder(currentOrder);
  }

  private void processCurrentOrder() {
    if (batteryAssemblyLineAdapter
        .getAssemblyStatus(currentOrder.getId())
        .equals(AssemblyStatus.ASSEMBLED)) {
      LOGGER.info("Battery order assembled: " + currentOrder.getId().toString());
      notifyBatteryCompleted(currentOrder);
      this.currentOrder = null;
    } else {
      currentOrderRemainingTimeToProduce = currentOrderRemainingTimeToProduce.subtractWeeks(1);
    }
  }

  private ProductionTime computeAssemblyDelay(Order order) {
    ProductionTime remainingTimeToProduce = computeRemainingTimeToProduce(order.getId());
    return remainingTimeToProduce.subtract(order.getBatteryOrder().getProductionTime());
  }

  private ProductionTime computeRemainingTimeToProduceBasedOnPositionInQueue(OrderId orderId) {
    int remainingTimeToProduce = 0;
    for (Order order : orderQueue) {
      remainingTimeToProduce += order.getBatteryOrder().getProductionTime().inWeeks();
      if (order.getId().equals(orderId)) break;
    }
    return new ProductionTime(
        remainingTimeToProduce + currentOrderRemainingTimeToProduce.inWeeks());
  }
}
