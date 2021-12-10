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
import java.util.Optional;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccumulateModelWarehouseStrategy extends ModelInventoryObservable
    implements ModelWarehouseStrategy, ModelAssembledObserver {
  private static final Logger LOGGER = LogManager.getLogger();

  private final List<ModelOrder> modelAssemblyCycle;
  private final ModelManufacturer modelManufacturer;
  private final ModelInventory modelInventory;
  private final List<Order> orderQueue = new ArrayList<>();

  private String currentModelTypeBeingAssembled;

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
    }
  }

  @Override
  public AssemblyTime computeRemainingTimeToProduce(OrderId orderId) {
    return new AssemblyTime(1);
    //    Optional<Order> fetchedOrder = fetchOrder(orderId);
    //    if (fetchedOrder.isEmpty()) {
    //      return new AssemblyTime(0);
    //    }
    //    ModelOrder fetchedModel = fetchedOrder.get().getModelOrder();
    //    String orderModelType = fetchedModel.getModelType();
    //
    //    int numberOfModelsOfTypeInQueue = computeNumberOfModelsOfTypesInQueue(orderModelType);
    //    return timeBeforeAssemblingModelType(numberOfModelsOfTypeInQueue, fetchedModel);
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

  private int computeNumberOfModelsOfTypesInQueue(String orderModelType) {
    int count = 0;
    for (Order order : orderQueue) {
      if (order.getModelOrder().getModelType().equals(orderModelType)) {
        count++;
      }
    }

    return count;
  }

  private AssemblyTime timeUntilModelTypeIsOnTopAndAssembled(String orderModelType) {
    return new AssemblyTime(1);
    //    if (orderModelType.equals(currentModelTypeBeingAssembled.getModelType())) {
    //      return currentModelRemainingAssemblyTime;
    //    }
    //
    //    AssemblyTime timeRemainingBeforeAssemblingModel = currentModelRemainingAssemblyTime;
    //    ModelOrder nextModelOrderInLine =
    //        computeNextModelTypeToBeAssembled(currentModelTypeBeingAssembled.getModelType());
    //    while (!nextModelOrderInLine.getModelType().equals(orderModelType)) {
    //      timeRemainingBeforeAssemblingModel =
    //          timeRemainingBeforeAssemblingModel.add(nextModelOrderInLine.getAssemblyTime());
    //      nextModelOrderInLine =
    // computeNextModelTypeToBeAssembled(nextModelOrderInLine.getModelType());
    //    }
    //
    //    return timeRemainingBeforeAssemblingModel;
  }

  private AssemblyTime computeTimeToAssembleEntireCycle() {
    AssemblyTime timeToAssemblyEntireCycle = new AssemblyTime(0);
    for (ModelOrder modelOrder : modelAssemblyCycle) {
      timeToAssemblyEntireCycle = timeToAssemblyEntireCycle.add(modelOrder.getAssemblyTime());
    }

    return timeToAssemblyEntireCycle;
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

  private AssemblyTime timeBeforeAssemblingModelType(
      Integer numberOfModelsOfTypeInQueue, ModelOrder modelOrder) {
    return null;
    //    String orderModelType = modelOrder.getModelType();
    //
    //    AssemblyTime timeBeforeAssemblingModelType =
    //        timeUntilModelTypeIsOnTopAndAssembled(orderModelType);
    //    AssemblyTime timeToAssembleFullCycles =
    //        new AssemblyTime(
    //            computeTimeToAssembleEntireCycle().inWeeks() * (numberOfModelsOfTypeInQueue - 1));
    //
    //    if (currentModelTypeBeingAssembled.getModelType().equals(orderModelType)) {
    //      if (numberOfModelsOfTypeInQueue == 1) {
    //        return currentModelRemainingAssemblyTime;
    //      }
    //      return timeBeforeAssemblingModelType.add(timeToAssembleFullCycles);
    //    }
    //
    //    return timeBeforeAssemblingModelType
    //        .add(timeToAssembleFullCycles)
    //        .add(modelOrder.getAssemblyTime());
  }

  private Optional<Order> fetchOrder(OrderId orderId) {
    return orderQueue.stream().filter(order -> order.getId().equals(orderId)).findFirst();
  }
}
