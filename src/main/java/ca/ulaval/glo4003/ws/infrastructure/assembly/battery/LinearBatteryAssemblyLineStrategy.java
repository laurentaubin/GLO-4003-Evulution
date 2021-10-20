package ca.ulaval.glo4003.ws.infrastructure.assembly.battery;

import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryAssemblyObservable;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;

import java.util.LinkedList;
import java.util.Queue;

public class LinearBatteryAssemblyLineStrategy extends BatteryAssemblyObservable
    implements BatteryAssemblyLineStrategy {

  private final BatteryAssemblyLine batteryAssemblyLine;
  private final CommandIdFactory commandIdFactory;
  private final Queue<Order> orderQueue = new LinkedList<>();

  private Order currentOrder;
  private ProductionTime currentOrderRemainingTimeToProduce;

  public LinearBatteryAssemblyLineStrategy(
      BatteryAssemblyLine batteryAssemblyLine, CommandIdFactory commandIdFactory) {
    this.batteryAssemblyLine = batteryAssemblyLine;
    this.commandIdFactory = commandIdFactory;
  }

  @Override
  public void advance() {
    batteryAssemblyLine.advance();
    if (isAnOrderBeingAssembled()) {
      processCurrentOrder();
    }
    if (isNextOrderReadyToBeAssembled()) {
      sendNextOrderToBeAssembled();
    }
  }

  @Override
  public void addOrder(Order order) {
    if (orderQueue.isEmpty() && isAssemblyLineFree()) {
      sendOrderToBeAssembled(order);
      return;
    }
    orderQueue.add(order);

    if (computeRemainingTimeToProduce(order.getId()).inWeeks()
        > order.getBattery().getProductionTime().inWeeks()) {
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

  private boolean isAssemblyLineFree() {
    if (currentOrder == null) {
      return true;
    }
    CommandID commandId = commandIdFactory.createFromOrderId(currentOrder.getId());
    BuildStatus currentOrderBuildStatus = batteryAssemblyLine.getBuildStatus(commandId);
    return currentOrderBuildStatus == null || currentOrderBuildStatus == BuildStatus.ASSEMBLED;
  }

  private boolean isAnOrderBeingAssembled() {
    return this.currentOrder != null;
  }

  private boolean isNextOrderReadyToBeAssembled() {
    return isAssemblyLineFree() && !orderQueue.isEmpty();
  }

  private void sendOrderToBeAssembled(Order order) {
    CommandID commandId = commandIdFactory.createFromOrderId(order.getId());
    currentOrder = order;
    currentOrderRemainingTimeToProduce = order.getBattery().getProductionTime();
    batteryAssemblyLine.newBatteryCommand(commandId, order.getBattery().getType());
  }

  private void sendNextOrderToBeAssembled() {
    currentOrder = orderQueue.remove();
    currentOrderRemainingTimeToProduce = currentOrder.getBattery().getProductionTime();
    CommandID nextInLineCommandId = commandIdFactory.createFromOrderId(currentOrder.getId());
    batteryAssemblyLine.newBatteryCommand(nextInLineCommandId, currentOrder.getBattery().getType());
  }

  private void processCurrentOrder() {
    CommandID commandId = commandIdFactory.createFromOrderId(currentOrder.getId());
    if (batteryAssemblyLine.getBuildStatus(commandId) == BuildStatus.ASSEMBLED) {
      notifyBatteryCompleted(currentOrder);
      this.currentOrder = null;
    } else {
      currentOrderRemainingTimeToProduce.subtractWeeks(1);
    }
  }

  private ProductionTime computeRemainingTimeToProduceBasedOnPositionInQueue(OrderId orderId) {
    int remainingTimeToProduce = 0;
    for (Order order : orderQueue) {
      remainingTimeToProduce += order.getBattery().getProductionTime().inWeeks();
      if (order.getId().equals(orderId)) break;
    }
    return new ProductionTime(
        remainingTimeToProduce + currentOrderRemainingTimeToProduce.inWeeks());
  }
}
