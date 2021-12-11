package ca.ulaval.glo4003.ws.domain.warehouse.model.strategy;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventory;
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
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccumulateModelWarehouseStrategy extends ModelInventoryObservable
    implements ModelWarehouseStrategy, ModelAssembledObserver {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final Logger LOGGER = LogManager.getLogger();

  private final List<ModelOrder> modelAssemblyCycle;
  private final ModelManufacturer modelManufacturer;
  private final ModelInventory modelInventory;
  private final List<Order> orderQueue = new ArrayList<>();

  private String currentModelTypeBeingAssembled;

  public AccumulateModelWarehouseStrategy(
      List<ModelOrder> modelAssemblyCycle, ModelManufacturer modelManufacturer) {
    this(modelAssemblyCycle, modelManufacturer, serviceLocator.resolve(ModelInventory.class));
  }

  public AccumulateModelWarehouseStrategy(
      List<ModelOrder> modelAssemblyCycle,
      ModelManufacturer modelManufacturer,
      ModelInventory modelInventory) {
    this.modelAssemblyCycle = modelAssemblyCycle;
    this.modelManufacturer = modelManufacturer;
    this.modelInventory = modelInventory;
    sendFirstModelToBeAssembled();
  }

  @Override
  public void addOrder(Order order) {
    LOGGER.info(
        String.format(
            "Model order received for order %s with model type of %s",
            order.getId(), order.getModelOrder().getModelType()));
    String modelType = order.getModelOrder().getModelType();
    if (modelInventory.isInStock(modelType)) {
      LOGGER.info(String.format("Model for order %s already in stock", order.getId()));
      modelInventory.removeOne(order.getModelOrder().getModelType());
      notifyModelInStock(order);
    } else {
      LOGGER.info(
          String.format(
              "Model for order %s not in stock, adding to order to queue", order.getId()));
      orderQueue.add(order);

      try {
        order.addAssemblyDelay(computeRemainingTimeToProduce(order.getId()));
        notifyModelDelay(order);
      } catch (InvalidModelQuantityInQueueException exception) {
        LOGGER.error(
            "Tried to compute order remaining time with wrong model type quantity", exception);
      }
    }
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
        orderQueue.stream()
            .filter(
                orderInQueue ->
                    Objects.equals(
                        orderInQueue.getModelOrder().getModelType(),
                        order.getModelOrder().getModelType()))
            .collect(Collectors.toList());
    return ordersOfRequestedTypeInQueue.indexOf(order);
  }

  @Override
  public void listenToModelAssembled(ModelOrder modelOrder) {
    System.out.println("MODEL ASSEMBLED: " + modelOrder.getModelType());
    String assembledModelType = modelOrder.getModelType();
    if (isModelNeededByAnOrder(assembledModelType)) {
      processFirstOrderNeedingModelType(assembledModelType);
    } else {
      modelInventory.addOne(assembledModelType);
    }
    sendNextModelToBeAssembled();
  }

  @Override
  public List<Order> getActiveOrders() {
    return new ArrayList<>(orderQueue);
  }

  private void sendFirstModelToBeAssembled() {
    ModelOrder modelOrder = modelAssemblyCycle.get(0);
    currentModelTypeBeingAssembled = modelOrder.getModelType();
    modelManufacturer.addOrder(modelOrder);
  }

  private void sendNextModelToBeAssembled() {
    ModelOrder modelOrder = computeNextModelTypeToBeAssembled(currentModelTypeBeingAssembled);
    currentModelTypeBeingAssembled = modelOrder.getModelType();
    modelManufacturer.addOrder(modelOrder);
  }

  private ModelOrder computeNextModelTypeToBeAssembled(String currentModelTypeToAssemble) {
    int currentModelPositionInModelAssemblyCycle =
        computeCurrentModelPositionInModelAssemblyCycle(currentModelTypeToAssemble);
    if (isLastModel(currentModelPositionInModelAssemblyCycle)) {
      return modelAssemblyCycle.get(0);
    }
    return modelAssemblyCycle.get(currentModelPositionInModelAssemblyCycle + 1);
  }

  private void processFirstOrderNeedingModelType(String modelType) {
    for (Order order : orderQueue) {
      if (order.getModelOrder().getModelType().equals(modelType)) {
        LOGGER.info(
            String.format("Model for order %s assembled, removed order from queue", order.getId()));
        orderQueue.remove(order);
        notifyModelInStock(order);
        break;
      }
    }
  }

  private boolean isModelNeededByAnOrder(String modelType) {
    for (Order order : orderQueue) {
      if (order.getModelOrder().getModelType().equals(modelType)) {
        return true;
      }
    }
    return false;
  }

  private int computeCurrentModelPositionInModelAssemblyCycle(String currentModelType) {
    return IntStream.range(0, modelAssemblyCycle.size())
        .filter(index -> modelAssemblyCycle.get(index).getModelType().equals(currentModelType))
        .findFirst()
        .orElse(-1);
  }

  private boolean isLastModel(int modelPositionInModelAssemblyOrder) {
    return modelPositionInModelAssemblyOrder == modelAssemblyCycle.size() - 1;
  }

  private Optional<Order> fetchOrder(OrderId orderId) {
    return orderQueue.stream().filter(order -> order.getId().equals(orderId)).findFirst();
  }
}
