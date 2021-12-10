package ca.ulaval.glo4003.ws.domain.warehouse.model.strategy;

import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventoryObservable;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JustInTimeModelWarehouseStrategy extends ModelInventoryObservable
    implements ModelWarehouseStrategy, ModelAssembledObserver {
  private static final Logger LOGGER = LogManager.getLogger();

  private final ModelManufacturer modelManufacturer;
  private final ModelInventory modelInventory;
  private final List<Order> orderQueue = new ArrayList<>();

  public JustInTimeModelWarehouseStrategy(
      ModelManufacturer modelManufacturer,
      ModelInventory modelInventory,
      List<ModelOrder> initialModelAssemblyOrder) {
    this.modelManufacturer = modelManufacturer;
    this.modelInventory = modelInventory;
    sendInitialModelsToBeAssembled(initialModelAssemblyOrder);
  }

  @Override
  public void addOrder(Order order) {
    LOGGER.info(
        String.format(
            "Model order received for order %s and model type is %s",
            order.getId(), order.getModelOrder().getModelType()));
    String modelTypeFromOrder = order.getModelOrder().getModelType();
    if (modelInventory.isInStock(modelTypeFromOrder)) {
      LOGGER.info(String.format("Model for order %s already in stock", order.getId()));
      notifyModelInStock(order);
      modelInventory.removeOne(modelTypeFromOrder);
    } else {
      LOGGER.info(
          String.format(
              "Model for order %s not in stock, adding to order to queue", order.getId()));
      orderQueue.add(order);
    }
    LOGGER.info(
        String.format("Model %s added to model orders", order.getModelOrder().getModelType()));
    modelManufacturer.addOrder(order.getModelOrder());
    //    modelOrders.add(order.getModelOrder());
  }

  @Override
  public AssemblyTime computeRemainingTimeToProduce(OrderId orderId) {
    return null;
    //    Optional<Order> optionalRequestedOrder = fetchOrder(orderId);
    //    if (optionalRequestedOrder.isEmpty()) {
    //      return new AssemblyTime(0);
    //    }
    //    Order order = optionalRequestedOrder.get();
    //    String modelType = order.getModelOrder().getModelType();
    //
    //    Integer positionInQueue = getPositionInQueueOfOrder(order);
    //
    //    if (orderIsBeingAssembled(modelType, positionInQueue)) {
    //      return currentOrderRemainingTimeToProduce;
    //    }
    //
    //    return timeUntilPositionInQueueIsProduced(positionInQueue, modelType);
  }

  @Override
  public List<Order> getActiveOrders() {
    return new ArrayList<>(orderQueue);
  }

  @Override
  public void listenToModelAssembled(ModelOrder modelOrder) {
    System.out.println("MODEL ASSEMBLED: " + modelOrder.getModelType());
    String modelTypeAssembled = modelOrder.getModelType();
    if (isModelNeededByAnOrder(modelTypeAssembled)) {
      processFirstOrderWaitingForModelType(modelTypeAssembled);
    } else {
      modelInventory.addOne(modelTypeAssembled);
    }
  }

  private void sendInitialModelsToBeAssembled(List<ModelOrder> initialModelAssemblyOrder) {
    for (ModelOrder modelOrder : initialModelAssemblyOrder) {
      modelManufacturer.addOrder(modelOrder);
    }
  }

  private boolean isModelNeededByAnOrder(String assembledModelType) {
    for (Order order : orderQueue) {
      if (order.getModelOrder().getModelType().equals(assembledModelType)) {
        return true;
      }
    }
    return false;
  }

  private void processFirstOrderWaitingForModelType(String assembledModelType) {
    for (Order order : orderQueue) {
      if (order.getModelOrder().getModelType().equals(assembledModelType)) {
        LOGGER.info(
            String.format("Model for order %s assembled, removed order from queue", order.getId()));
        notifyModelInStock(order);
        orderQueue.remove(order);
        return;
      }
    }
  }

  private Optional<Order> fetchOrder(OrderId orderId) {
    return orderQueue.stream().filter(order -> order.getId() == orderId).findFirst();
  }

  private AssemblyTime timeUntilPositionInQueueIsProduced(
      Integer positionInQueue, String modelType) {
    return null;
    //    AssemblyTime remainingTime = new AssemblyTime(0);
    //    Integer modelOrdersOfTypeRequestedOrderType = 0;
    //    if (currentModelBeingAssembled.getModelType().equals(modelType)) {
    //      remainingTime = new AssemblyTime(currentOrderRemainingTimeToProduce.inWeeks());
    //      modelOrdersOfTypeRequestedOrderType = 1;
    //    }
    //    for (ModelOrder modelOrder : modelOrders) {
    //      remainingTime = remainingTime.add(modelOrder.getAssemblyTime());
    //      if (modelOrder.getModelType().equals(modelType)) {
    //        if (modelOrdersOfTypeRequestedOrderType.equals(positionInQueue)) {
    //          return remainingTime;
    //        } else {
    //          modelOrdersOfTypeRequestedOrderType += 1;
    //        }
    //      }
    //    }
    //    return remainingTime;
  }

  private Integer getPositionInQueueOfOrder(Order order) {
    List<Order> ordersOfRequestedTypeInQueue =
        orderQueue.stream()
            .filter(
                orderInQueue ->
                    Objects.equals(
                        orderInQueue.getModelOrder().getModelType(),
                        order.getModelOrder().getModelType()))
            .collect(Collectors.toList());
    return ordersOfRequestedTypeInQueue.indexOf(order);
  }
}
