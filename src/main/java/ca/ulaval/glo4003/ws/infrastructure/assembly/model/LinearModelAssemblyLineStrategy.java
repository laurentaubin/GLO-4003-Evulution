package ca.ulaval.glo4003.ws.infrastructure.assembly.model;

import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssembledObservable;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderQueue;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;

public class LinearModelAssemblyLineStrategy extends ModelAssembledObservable
    implements ModelAssemblyLineStrategy {

  private VehicleAssemblyLine vehicleAssemblyLine;
  private CommandIdFactory commandIdFactory;
  private OrderQueue orderQueue;

  private Order currentOrder;

  public LinearModelAssemblyLineStrategy(
      VehicleAssemblyLine vehicleAssemblyLine,
      CommandIdFactory commandIdFactory,
      OrderQueue orderQueue) {
    this.vehicleAssemblyLine = vehicleAssemblyLine;
    this.commandIdFactory = commandIdFactory;
    this.orderQueue = orderQueue;
  }

  @Override
  public void advance() {
    vehicleAssemblyLine.advance();
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
    // TODO To be implemented
    return 0;
  }

  private boolean isAssemblyLineFree() {
    if (currentOrder == null) {
      return true;
    }
    CommandID commandId = commandIdFactory.createFromOrderId(currentOrder.getId());
    BuildStatus currentOrderBuildStatus = vehicleAssemblyLine.getBuildStatus(commandId);
    return currentOrderBuildStatus == null || currentOrderBuildStatus == BuildStatus.ASSEMBLED;
  }

  private void sendOrderToBeAssembled(Order order) {
    CommandID commandId = commandIdFactory.createFromOrderId(order.getId());
    currentOrder = order;
    vehicleAssemblyLine.newCarCommand(commandId, order.getModel().getName());
  }

  private boolean isAnOrderBeingAssembled() {
    return this.currentOrder != null;
  }

  private boolean isNextOrderReadyToBeAssembled() {
    return isAssemblyLineFree() && !orderQueue.isEmpty();
  }

  private void sendNextOrderToBeAssembled() {
    Order nextOrder = orderQueue.getNextInLine();
    CommandID nextInLineCommandId = commandIdFactory.createFromOrderId(nextOrder.getId());
    vehicleAssemblyLine.newCarCommand(nextInLineCommandId, nextOrder.getModel().getName());
  }

  private void processCurrentOrder() {
    CommandID commandId = commandIdFactory.createFromOrderId(currentOrder.getId());
    if (vehicleAssemblyLine.getBuildStatus(commandId) == BuildStatus.ASSEMBLED) {
      notifyModelAssembled(currentOrder);
      this.currentOrder = null;
    }
  }
}
