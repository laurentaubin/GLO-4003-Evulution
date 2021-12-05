package ca.ulaval.glo4003.ws.domain.assembly.model.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyObservable;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnDemandModelAssemblyLineStrategy extends ModelAssemblyObservable
    implements ModelAssemblyLineStrategy {

  private static final Logger LOGGER = LogManager.getLogger();

  private final ModelAssemblyLineAdapter modelAssemblyLineAdapter;

  private final Queue<Order> orderQueue = new LinkedList<>();

  private Order currentOrder;
  private AssemblyTime currentOrderRemainingTimeToProduce;

  public OnDemandModelAssemblyLineStrategy(ModelAssemblyLineAdapter modelAssemblyLineAdapter) {
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

    AssemblyTime assemblyDelay = computeAssemblyDelay(order);
    if (!assemblyDelay.isOver()) {
      order.addAssemblyDelay(assemblyDelay);
      notifyModelAssemblyDelay(order);
    }
  }

  @Override
  public AssemblyTime computeRemainingTimeToProduce(OrderId orderId) {
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
    currentOrderRemainingTimeToProduce = order.getModelOrder().getAssemblyTime();
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
    currentOrderRemainingTimeToProduce = currentOrder.getModelOrder().getAssemblyTime();
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

  private AssemblyTime computeAssemblyDelay(Order order) {
    AssemblyTime remainingTimeToProduce = computeRemainingTimeToProduce(order.getId());
    return remainingTimeToProduce.subtract(order.getModelOrder().getAssemblyTime());
  }

  private AssemblyTime computeRemainingTimeToProduceBasedOnPositionInQueue(OrderId orderId) {
    int remainingTimeToProduce = 0;
    for (Order order : orderQueue) {
      remainingTimeToProduce += order.getModelOrder().getAssemblyTime().inWeeks();
      if (order.getId().equals(orderId)) break;
    }
    return new AssemblyTime(remainingTimeToProduce + currentOrderRemainingTimeToProduce.inWeeks());
  }

  private boolean isCurrentOrderAssembled() {
    return modelAssemblyLineAdapter
        .getAssemblyStatus(currentOrder.getId())
        .equals(AssemblyStatus.ASSEMBLED);
  }

  @Override
  public List<Order> getActiveOrders() {
    List<Order> activeOrders = new ArrayList<>(orderQueue);
    if (isAnOrderBeingAssembled()) {
      activeOrders.add(0, currentOrder);
    }
    return activeOrders;
  }
}
