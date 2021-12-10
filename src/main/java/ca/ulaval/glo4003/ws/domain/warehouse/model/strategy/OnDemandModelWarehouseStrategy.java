package ca.ulaval.glo4003.ws.domain.warehouse.model.strategy;

import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventoryObservable;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnDemandModelWarehouseStrategy extends ModelInventoryObservable
    implements ModelWarehouseStrategy, ModelAssembledObserver {

  private static final Logger LOGGER = LogManager.getLogger();

  private final ModelManufacturer modelManufacturer;
  private final List<Order> ordersQueue = new ArrayList<>();

  public OnDemandModelWarehouseStrategy(ModelManufacturer modelManufacturer) {
    this.modelManufacturer = modelManufacturer;
  }

  @Override
  public void addOrder(Order order) {
    LOGGER.info(String.format("Model order received: %s", order.getId()));
    modelManufacturer.addOrder(order.getModelOrder());
    ordersQueue.add(order);

    AssemblyTime assemblyDelay = computeRemainingTimeToProduce(order.getId());

    if (!assemblyDelay.isOver()) {
      order.addAssemblyDelay(assemblyDelay);
      notifyModelDelay(order);
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
  public List<Order> getActiveOrders() {
    return new ArrayList<>(ordersQueue);
  }

  @Override
  public void listenToModelAssembled(ModelOrder modelOrder) {
    Order order = findFirstOrderWaitingForModelType(modelOrder.getModelType());
    notifyModelInStock(order);
    ordersQueue.remove(order);
  }

  private boolean isOrderFirstWaitingForModelType(OrderId orderId, String requiredModelType) {
    Order firstOrderWaitingForRequiredModelType =
        findFirstOrderWaitingForModelType(requiredModelType);
    return firstOrderWaitingForRequiredModelType.getId().equals(orderId);
  }

  private Order findFirstOrderWaitingForModelType(String modelType) {
    return ordersQueue.stream()
        .filter(order -> order.getModelOrder().getModelType().equals(modelType))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No order waiting for " + modelType));
  }

  private Order findOrder(OrderId orderId) {
    return ordersQueue.stream()
        .filter(order -> order.getId().equals(orderId))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Order does not exist or model already assembled"));
  }

  private AssemblyTime computeAssemblyTimeBeforeReceivingModelForOrder(Order order) {
    AssemblyTime timeBeforeReceivingModelTypeForOrder = new AssemblyTime(0);
    for (Order queuedOrder : ordersQueue) {
      timeBeforeReceivingModelTypeForOrder =
          timeBeforeReceivingModelTypeForOrder.add(queuedOrder.getModelOrder().getAssemblyTime());
      if (queuedOrder.getId().equals(order.getId())) {
        break;
      }
    }
    return timeBeforeReceivingModelTypeForOrder;
  }

  private AssemblyTime computeDelay(Order order) {
    AssemblyTime remainingTimeForNextModelOrderToBeAssembled =
        modelManufacturer.computeRemainingTimeToProduceNextModelType(
            order.getModelOrder().getModelType());
    if (isOrderFirstWaitingForModelType(order.getId(), order.getModelOrder().getModelType())) {
      return remainingTimeForNextModelOrderToBeAssembled;
    }
    AssemblyTime timeBeforeReceivingModelTypeForOrder =
        computeAssemblyTimeBeforeReceivingModelForOrder(order);
    return timeBeforeReceivingModelTypeForOrder.subtract(
        remainingTimeForNextModelOrderToBeAssembled);
  }
}
