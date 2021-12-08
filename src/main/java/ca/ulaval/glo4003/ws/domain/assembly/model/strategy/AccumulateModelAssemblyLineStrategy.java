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
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccumulateModelAssemblyLineStrategy extends ModelAssemblyObservable
    implements ModelAssemblyLineStrategy {
  private static final Logger LOGGER = LogManager.getLogger();

  private ModelOrder currentModelBeingAssembled;
  private AssemblyTime currentModelRemainingAssemblyTime;
  private final List<ModelOrder> modelAssemblyCycle;
  private final ModelAssemblyLineAdapter modelAssemblyLineAdapter;
  private final ModelInventory modelInventory;
  private final List<Order> orderQueue = new ArrayList<>();

  public AccumulateModelAssemblyLineStrategy(
      List<ModelOrder> modelAssemblyCycle,
      ModelAssemblyLineAdapter modelAssemblyLineAdapter,
      ModelInventory modelInventory) {
    this.modelAssemblyCycle = modelAssemblyCycle;
    this.modelAssemblyLineAdapter = modelAssemblyLineAdapter;
    this.modelInventory = modelInventory;
    sendFirstModelToBeAssembled();
  }

  public void advance() {
    modelAssemblyLineAdapter.advance();
    currentModelRemainingAssemblyTime.subtract(new AssemblyTime(1));
    if (isCurrentModelAssembled()) {
      LOGGER.info(String.format("Model %s assembled", currentModelBeingAssembled.getModelType()));
      if (isModelNeededByAnOrder(currentModelBeingAssembled.getModelType())) {
        notifyFirstOrderWaitingForModelType(currentModelBeingAssembled.getModelType());
      } else {
        modelInventory.addOne(currentModelBeingAssembled.getModelType());
      }
      sendNextModelToBeAssembled();
    }
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
      notifyModelAssembled(order);
    } else {
      LOGGER.info(
          String.format(
              "Model for order %s not in stock, adding to order to queue", order.getId()));
      orderQueue.add(order);
    }
  }

  @Override
  public AssemblyTime computeRemainingTimeToProduce(OrderId orderId) {
    Optional<Order> fetchedOrder = fetchOrder(orderId);
    if (fetchedOrder.isEmpty()) {
      return new AssemblyTime(0);
    }
    ModelOrder fetchedModel = fetchedOrder.get().getModelOrder();
    String orderModelType = fetchedModel.getModelType();

    int numberOfModelsOfTypeInQueue = computeNumberOfModelsOfTypesInQueue(orderModelType);
    return timeBeforeAssemblingModelType(numberOfModelsOfTypeInQueue, fetchedModel);
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
    if (orderModelType.equals(currentModelBeingAssembled.getModelType())) {
      return currentModelRemainingAssemblyTime;
    }

    AssemblyTime timeRemainingBeforeAssemblingModel = currentModelRemainingAssemblyTime;
    ModelOrder nextModelOrderInLine =
        computeNextModelTypeToBeAssembled(currentModelBeingAssembled.getModelType());
    while (!nextModelOrderInLine.getModelType().equals(orderModelType)) {
      timeRemainingBeforeAssemblingModel =
          timeRemainingBeforeAssemblingModel.add(nextModelOrderInLine.getAssemblyTime());
      nextModelOrderInLine = computeNextModelTypeToBeAssembled(nextModelOrderInLine.getModelType());
    }

    return timeRemainingBeforeAssemblingModel;
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
    modelAssemblyLineAdapter.addOrder(modelOrder);
    currentModelBeingAssembled = modelOrder;
    currentModelRemainingAssemblyTime = modelOrder.getAssemblyTime();
  }

  private void sendNextModelToBeAssembled() {
    ModelOrder model = computeNextModelTypeToBeAssembled(currentModelBeingAssembled.getModelType());
    modelAssemblyLineAdapter.addOrder(model);
    currentModelBeingAssembled = model;
    currentModelRemainingAssemblyTime = model.getAssemblyTime();
  }

  private ModelOrder computeNextModelTypeToBeAssembled(String currentModelTypeToAssemble) {
    int currentModelPositionInModelAssemblyCycle =
        computeCurrentModelPositionInModelAssemblyCycle(currentModelTypeToAssemble);
    if (isLastModel(currentModelPositionInModelAssemblyCycle)) {
      return modelAssemblyCycle.get(0);
    }
    return modelAssemblyCycle.get(currentModelPositionInModelAssemblyCycle + 1);
  }

  private void notifyFirstOrderWaitingForModelType(String modelType) {
    for (Order order : orderQueue) {
      if (order.getModelOrder().getModelType().equals(modelType)) {
        LOGGER.info(
            String.format("Model for order %s assembled, removed order from queue", order.getId()));
        orderQueue.remove(order);
        notifyModelAssembled(order);
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

  private boolean isCurrentModelAssembled() {
    return modelAssemblyLineAdapter.getAssemblyStatus(currentModelBeingAssembled.getOrderId())
        == AssemblyStatus.ASSEMBLED;
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

  private AssemblyTime timeBeforeAssemblingModelType(Integer numberOfModelsOfTypeInQueue, ModelOrder modelOrder) {
    String orderModelType = modelOrder.getModelType();

    AssemblyTime timeBeforeAssemblingModelType =
            timeUntilModelTypeIsOnTopAndAssembled(orderModelType);
    AssemblyTime timeToAssembleFullCycles =
            new AssemblyTime(
                    computeTimeToAssembleEntireCycle().inWeeks() * (numberOfModelsOfTypeInQueue - 1));

    if (currentModelBeingAssembled.getModelType().equals(orderModelType)) {
      if (numberOfModelsOfTypeInQueue == 1) {
        return currentModelRemainingAssemblyTime;
      }
      return timeBeforeAssemblingModelType.add(timeToAssembleFullCycles);
    }

    return timeBeforeAssemblingModelType
            .add(timeToAssembleFullCycles)
            .add(modelOrder.getAssemblyTime());
  }

  private Optional<Order> fetchOrder(OrderId orderId) {
    return orderQueue.stream().filter(order -> order.getId().equals(orderId)).findFirst();
  }
}
