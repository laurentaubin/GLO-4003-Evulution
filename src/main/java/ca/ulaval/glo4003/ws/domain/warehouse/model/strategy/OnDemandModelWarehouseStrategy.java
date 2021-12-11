package ca.ulaval.glo4003.ws.domain.warehouse.model.strategy;

import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventoryObservable;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.infrastructure.manufacturer.model.exception.InvalidModelQuantityInQueueException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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

    try {
      AssemblyTime assemblyDelay = computeAssemblyDelay(order.getId());
      if (!assemblyDelay.isOver()) {
        order.addAssemblyDelay(assemblyDelay);
        notifyModelDelay(order);
      }
    } catch (InvalidModelQuantityInQueueException exception) {
      LOGGER.error(
          "Tried to compute order remaining time with wrong model type quantity", exception);
    }
  }

  private AssemblyTime computeAssemblyDelay(OrderId orderId) {
    Optional<Order> optionalRequestedOrder = fetchOrder(orderId);
    if (optionalRequestedOrder.isEmpty()) {
      return new AssemblyTime(0);
    }

    AssemblyTime assemblyTime = computeRemainingTimeToProduce(orderId);
    return assemblyTime.subtract(optionalRequestedOrder.get().getModelOrder().getAssemblyTime());
  }

  private AssemblyTime computeRemainingTimeToProduce(OrderId orderId) {
    Optional<Order> optionalRequestedOrder = fetchOrder(orderId);
    if (optionalRequestedOrder.isEmpty()) {
      return new AssemblyTime(0);
    }
    Order order = optionalRequestedOrder.get();

    Integer positionInQueue = getPositionInQueueOfOrder(order);
    String modelType = order.getModelOrder().getModelType();

    return modelManufacturer.computeTimeToProduceQuantityOfModel(positionInQueue + 1, modelType);
  }

  private Integer getPositionInQueueOfOrder(Order order) {
    List<Order> ordersOfRequestedTypeInQueue =
        ordersQueue.stream()
            .filter(
                orderInQueue ->
                    Objects.equals(
                        orderInQueue.getModelOrder().getModelType(),
                        order.getModelOrder().getModelType()))
            .collect(Collectors.toList());
    return ordersOfRequestedTypeInQueue.indexOf(order);
  }

  private Optional<Order> fetchOrder(OrderId orderId) {
    return ordersQueue.stream().filter(order -> order.getId() == orderId).findFirst();
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

  private Order findFirstOrderWaitingForModelType(String modelType) {
    return ordersQueue.stream()
        .filter(order -> order.getModelOrder().getModelType().equals(modelType))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No order waiting for " + modelType));
  }
}
