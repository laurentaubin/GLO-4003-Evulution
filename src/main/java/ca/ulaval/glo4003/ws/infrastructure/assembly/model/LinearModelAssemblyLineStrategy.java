package ca.ulaval.glo4003.ws.infrastructure.assembly.model;

import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyObservable;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;

import java.util.LinkedList;
import java.util.Queue;

public class LinearModelAssemblyLineStrategy extends ModelAssemblyObservable
    implements ModelAssemblyLineStrategy {

  private final VehicleAssemblyLine vehicleAssemblyLine;
  private final CommandIdFactory commandIdFactory;
  private final Queue<Order> orderQueue = new LinkedList<>();

  private Order currentOrder;
  private ProductionTime currentOrderRemainingTimeToProduce;

  public LinearModelAssemblyLineStrategy(
      VehicleAssemblyLine vehicleAssemblyLine, CommandIdFactory commandIdFactory) {
    this.vehicleAssemblyLine = vehicleAssemblyLine;
    this.commandIdFactory = commandIdFactory;
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
      return;
    }
    orderQueue.add(order);

    if (computeRemainingTimeToProduce(order.getId()).inWeeks()
        > order.getModel().getProductionTime().inWeeks()) {
      notifyModelAssemblyDelay(order);
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
    BuildStatus currentOrderBuildStatus = vehicleAssemblyLine.getBuildStatus(commandId);
    return currentOrderBuildStatus == null || currentOrderBuildStatus == BuildStatus.ASSEMBLED;
  }

  private void sendOrderToBeAssembled(Order order) {
    CommandID commandId = commandIdFactory.createFromOrderId(order.getId());
    currentOrder = order;
    currentOrderRemainingTimeToProduce = order.getModel().getProductionTime();
    vehicleAssemblyLine.newCarCommand(commandId, order.getModel().getName());
  }

  private boolean isAnOrderBeingAssembled() {
    return this.currentOrder != null;
  }

  private boolean isNextOrderReadyToBeAssembled() {
    return isAssemblyLineFree() && !orderQueue.isEmpty();
  }

  private void sendNextOrderToBeAssembled() {
    currentOrder = orderQueue.remove();
    currentOrderRemainingTimeToProduce = currentOrder.getModel().getProductionTime();
    CommandID nextInLineCommandId = commandIdFactory.createFromOrderId(currentOrder.getId());
    vehicleAssemblyLine.newCarCommand(nextInLineCommandId, currentOrder.getModel().getName());
  }

  private void processCurrentOrder() {
    CommandID commandId = commandIdFactory.createFromOrderId(currentOrder.getId());
    if (vehicleAssemblyLine.getBuildStatus(commandId) == BuildStatus.ASSEMBLED) {
      notifyModelAssembled(currentOrder);
      currentOrder = null;
    } else {
      currentOrderRemainingTimeToProduce.subtractWeeks(1);
    }
  }

  private ProductionTime computeRemainingTimeToProduceBasedOnPositionInQueue(OrderId orderId) {
    int remainingTimeToProduce = 0;
    for (Order order : orderQueue) {
      remainingTimeToProduce += order.getModel().getProductionTime().inWeeks();
      if (order.getId().equals(orderId)) break;
    }
    return new ProductionTime(
        remainingTimeToProduce + currentOrderRemainingTimeToProduce.inWeeks());
  }
}
