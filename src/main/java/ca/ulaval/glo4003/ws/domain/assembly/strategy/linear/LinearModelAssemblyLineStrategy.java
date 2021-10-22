package ca.ulaval.glo4003.ws.domain.assembly.strategy.linear;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyObservable;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

public class LinearModelAssemblyLineStrategy extends ModelAssemblyObservable
    implements ModelAssemblyLineStrategy {

  private final AssemblyLineAdapter modelAssemblyLineAdapter;
  private final Queue<Order> orderQueue = new LinkedList<>();

  private Order currentOrder;
  private ProductionTime currentOrderRemainingTimeToProduce;

  private static final Logger LOGGER = LogManager.getLogger();

  public LinearModelAssemblyLineStrategy(AssemblyLineAdapter modelAssemblyLineAdapter) {
    this.modelAssemblyLineAdapter = modelAssemblyLineAdapter;
  }

  @Override
  public void advance() {
    modelAssemblyLineAdapter.advance();
    if (isAnOrderBeingAssembled()) {
      processCurrentOrder();
    }
    if (isNextOrderReadyToBeAssembled()) {
      sendNextOrderToBeAssembled();
    }
  }

  @Override
  public void addOrder(Order order) {
    LOGGER.info(String.format("Model order received: %s", order.getId()));
    if (orderQueue.isEmpty() && isAssemblyLineFree()) {
      sendOrderToBeAssembled(order);
      return;
    }
    orderQueue.add(order);

    ProductionTime assemblyDelay = computeAssemblyDelay(order);
    if (!assemblyDelay.isOver()) {
      order.addAssemblyDelay(assemblyDelay);
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
    return isCurrentOrderAssembled();
  }

  private void sendOrderToBeAssembled(Order order) {
    currentOrder = order;
    currentOrderRemainingTimeToProduce = order.getModel().getProductionTime();
    modelAssemblyLineAdapter.addOrder(currentOrder);
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
    modelAssemblyLineAdapter.addOrder(currentOrder);
  }

  private void processCurrentOrder() {
    if (isCurrentOrderAssembled()) {
      LOGGER.info("Model order assembled: " + currentOrder.getId().toString());
      notifyModelAssembled(currentOrder);
      currentOrder = null;
    } else {
      currentOrderRemainingTimeToProduce = currentOrderRemainingTimeToProduce.subtractWeeks(1);
    }
  }

  private ProductionTime computeAssemblyDelay(Order order) {
    ProductionTime remainingTimeToProduce = computeRemainingTimeToProduce(order.getId());
    return remainingTimeToProduce.subtract(order.getModel().getProductionTime());
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

  private boolean isCurrentOrderAssembled() {
    return modelAssemblyLineAdapter
        .getAssemblyStatus(currentOrder.getId())
        .equals(AssemblyStatus.ASSEMBLED);
  }
}
