package ca.ulaval.glo4003.ws.domain.assembly.model.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyObservable;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JustInTimeModelAssemblyStrategy extends ModelAssemblyObservable
    implements ModelAssemblyLineStrategy {
  private static final Logger LOGGER = LogManager.getLogger();

  private ModelOrder currentModelBeingAssembled;
  private AssemblyTime currentOrderRemainingTimeToProduce;
  private final ModelAssemblyLineAdapter modelAssemblyLineAdapter;
  private final ModelInventory modelInventory;
  private final List<ModelOrder> modelOrders = new ArrayList<>();
  private final List<Order> orderQueue = new ArrayList<>();

  public JustInTimeModelAssemblyStrategy(
      ModelAssemblyLineAdapter modelAssemblyLineAdapter,
      ModelInventory modelInventory,
      List<ModelOrder> initialModelAssemblyOrder) {
    this.modelAssemblyLineAdapter = modelAssemblyLineAdapter;
    this.modelInventory = modelInventory;
    sendInitialModelsToBeAssembled(initialModelAssemblyOrder);
  }

  @Override
  public void advance() {
    modelAssemblyLineAdapter.advance();
    if (currentModelBeingAssembled == null && modelOrders.isEmpty()) {
      LOGGER.info("Nothing to be assembled and no pending order");
      return;
    }

    if (currentOrderRemainingTimeToProduce != null) {
      currentOrderRemainingTimeToProduce =
          currentOrderRemainingTimeToProduce.subtract(new AssemblyTime(1));
      if (currentOrderRemainingTimeToProduce.equals(new AssemblyTime(0))) {
        currentOrderRemainingTimeToProduce = null;
      }
    }

    if (isCurrentModelAssembled()) {
      LOGGER.info(String.format("Model %s assembled", currentModelBeingAssembled.getModelType()));
      if (isModelNeededByAnOrder(currentModelBeingAssembled.getModelType())) {
        processFirstOrderWaitingForModelType(currentModelBeingAssembled.getModelType());
      } else {
        modelInventory.addOne(currentModelBeingAssembled.getModelType());
      }
      sendNextModelToBeAssembled();
    }
    if (!modelOrders.isEmpty() && currentModelBeingAssembled == null) {
      sendNextModelToBeAssembled();
    }
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
      notifyModelAssembled(order);
      modelInventory.removeOne(modelTypeFromOrder);
      LOGGER.info(
          String.format("Model %s added to model orders", order.getModelOrder().getModelType()));
      modelOrders.add(order.getModelOrder());
    } else {
      LOGGER.info(
          String.format(
              "Model for order %s not in stock, adding to order to queue", order.getId()));
      orderQueue.add(order);
      modelOrders.add(order.getModelOrder());
    }
  }

  @Override
  public AssemblyTime computeRemainingTimeToProduce(OrderId orderId) {
    Optional<Order> optionalRequestedOrder =
        orderQueue.stream().filter(order -> order.getId() == orderId).findFirst();
    if (optionalRequestedOrder.isEmpty()) {
      return new AssemblyTime(0);
    }

    Order requestedOrder = optionalRequestedOrder.get();
    String requestedOrderType = requestedOrder.getModelOrder().getModelType();
    List<Order> ordersOfRequestedTypeInQueue =
        orderQueue.stream()
            .filter(
                order -> Objects.equals(order.getModelOrder().getModelType(), requestedOrderType))
            .collect(Collectors.toList());
    Integer positionInQueue = ordersOfRequestedTypeInQueue.indexOf(requestedOrder);

    if (Objects.equals(currentModelBeingAssembled.getModelType(), requestedOrderType)
        && positionInQueue == 0) {
      return currentOrderRemainingTimeToProduce;
    }

    AssemblyTime remainingTime = new AssemblyTime(0);
    Integer modelOrdersOfTypeRequestedOrderType = 0;
    if (currentModelBeingAssembled.getModelType().equals(requestedOrderType)) {
      remainingTime = new AssemblyTime(currentOrderRemainingTimeToProduce.inWeeks());
      modelOrdersOfTypeRequestedOrderType = 1;
    }
    for (ModelOrder modelOrder : modelOrders) {
      remainingTime = remainingTime.add(modelOrder.getAssemblyTime());
      if (modelOrder.getModelType().equals(requestedOrderType)) {
        if (modelOrdersOfTypeRequestedOrderType.equals(positionInQueue)) {
          return remainingTime;
        } else {
          modelOrdersOfTypeRequestedOrderType += 1;
        }
      }
    }
    return remainingTime;
  }

  @Override
  public List<Order> getActiveOrders() {
    return new ArrayList<>(orderQueue);
  }

  private void sendInitialModelsToBeAssembled(List<ModelOrder> initialModelAssemblyOrder) {
    modelOrders.addAll(initialModelAssemblyOrder);
    sendNextModelToBeAssembled();
  }

  private void sendNextModelToBeAssembled() {
    if (!modelOrders.isEmpty()) {
      ModelOrder modelOrder = modelOrders.remove(0);
      currentModelBeingAssembled = modelOrder;
      currentOrderRemainingTimeToProduce = modelOrder.getAssemblyTime();
      modelAssemblyLineAdapter.addOrder(modelOrder);
      LOGGER.info(String.format("Model %s sent to be assembled", modelOrder.getModelType()));
    } else {
      currentModelBeingAssembled = null;
    }
  }

  private boolean isCurrentModelAssembled() {
    if (currentModelBeingAssembled == null) {
      return false;
    }
    return modelAssemblyLineAdapter.getAssemblyStatus(currentModelBeingAssembled.getOrderId())
        == AssemblyStatus.ASSEMBLED;
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
        notifyModelAssembled(order);
        orderQueue.remove(order);
        return;
      }
    }
  }
}
