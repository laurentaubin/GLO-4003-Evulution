package ca.ulaval.glo4003.ws.infrastructure.assembly.battery;

import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssembledObservable;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderQueue;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;

public class LinearBatteryAssemblyLineStrategy extends BatteryAssembledObservable
    implements BatteryAssemblyLineStrategy {

  private BatteryAssemblyLine batteryAssemblyLine;
  private CommandIdFactory commandIdFactory;
  private OrderQueue orderQueue;

  private Order currentOrder;

  public LinearBatteryAssemblyLineStrategy(
      BatteryAssemblyLine batteryAssemblyLine,
      CommandIdFactory commandIdFactory,
      OrderQueue orderQueue) {
    this.batteryAssemblyLine = batteryAssemblyLine;
    this.commandIdFactory = commandIdFactory;
    this.orderQueue = orderQueue;
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
    } else {
      orderQueue.addOrder(order);
    }
  }

  @Override
  public int computeRemainingTimeToProduce(OrderId orderId) {
    return 0;
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
    batteryAssemblyLine.newBatteryCommand(commandId, order.getBattery().getType());
  }

  private void sendNextOrderToBeAssembled() {
    Order nextOrder = orderQueue.getNextInLine();
    CommandID nextInLineCommandId = commandIdFactory.createFromOrderId(nextOrder.getId());
    batteryAssemblyLine.newBatteryCommand(nextInLineCommandId, nextOrder.getBattery().getType());
  }

  private void processCurrentOrder() {
    CommandID commandId = commandIdFactory.createFromOrderId(currentOrder.getId());
    if (batteryAssemblyLine.getBuildStatus(commandId) == BuildStatus.ASSEMBLED) {
      notifyBatteryCompleted(currentOrder);
      this.currentOrder = null;
    }
  }
}
