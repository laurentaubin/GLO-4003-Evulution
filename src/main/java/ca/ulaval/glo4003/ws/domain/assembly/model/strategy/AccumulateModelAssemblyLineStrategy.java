package ca.ulaval.glo4003.ws.domain.assembly.model.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyObservable;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccumulateModelAssemblyLineStrategy extends ModelAssemblyObservable
    implements ModelAssemblyLineStrategy {
  private static final Logger LOGGER = LogManager.getLogger();

  private ModelOrder currentModelBeingAssembled;
  private ProductionTime currentModelRemainingProductionTime;
  private final List<Model> modelAssemblyCycle;
  private final ModelAssemblyLineAdapter modelAssemblyLineAdapter;
  private final ModelInventory modelInventory;
  private final ModelOrderFactory modelOrderFactory;
  private final List<Order> orderQueue = new ArrayList<>();

  public AccumulateModelAssemblyLineStrategy(
      List<Model> modelAssemblyCycle,
      ModelAssemblyLineAdapter modelAssemblyLineAdapter,
      ModelInventory modelInventory,
      ModelOrderFactory modelOrderFactory) {
    this.modelAssemblyCycle = modelAssemblyCycle;
    this.modelAssemblyLineAdapter = modelAssemblyLineAdapter;
    this.modelInventory = modelInventory;
    this.modelOrderFactory = modelOrderFactory;
    sendFirstModelToBeAssembled();
  }

  public void advance() {
    modelAssemblyLineAdapter.advance();
    currentModelRemainingProductionTime.subtract(new ProductionTime(1));
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
  public ProductionTime computeRemainingTimeToProduce(OrderId orderId) {
    Optional<Order> fetchedOrder =
        orderQueue.stream().filter(order -> order.getId().equals(orderId)).findFirst();
    if (fetchedOrder.isEmpty()) {
      return new ProductionTime(0);
    }
    ModelOrder fetchedModel = fetchedOrder.get().getModelOrder();
    String orderModelType = fetchedModel.getModelType();

    int numberOfModelsOfTypeInQueue = computeNumberOfModelsOfTypesInQueue(orderModelType);
    ProductionTime timeBeforeAssemblingModelType =
        computeTimeRemainingBeforeAssemblingModelType(orderModelType);
    ProductionTime timeToAssembleFullCycles =
        new ProductionTime(
            computeTimeToAssembleEntireCycle().inWeeks() * (numberOfModelsOfTypeInQueue - 1));

    if (currentModelBeingAssembled.getModelType().equals(orderModelType)) {
      if (numberOfModelsOfTypeInQueue == 1) {
        return fetchedModel.getProductionTime();
      }

      return timeBeforeAssemblingModelType.add(timeToAssembleFullCycles);
    }

    return timeBeforeAssemblingModelType
        .add(timeToAssembleFullCycles)
        .add(fetchedModel.getProductionTime());
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

  public ProductionTime computeTimeRemainingBeforeAssemblingModelType(String orderModelType) {
    if (orderModelType.equals(currentModelBeingAssembled.getModelType())) {
      return currentModelRemainingProductionTime;
    }

    ProductionTime timeRemainingBeforeAssemblingModel = currentModelRemainingProductionTime;
    Model nextModelInLine =
        computeNextModelTypeToBeAssembled(currentModelBeingAssembled.getModelType());
    while (!nextModelInLine.getName().equals(orderModelType)) {
      timeRemainingBeforeAssemblingModel =
          timeRemainingBeforeAssemblingModel.add(nextModelInLine.getProductionTime());
      nextModelInLine = computeNextModelTypeToBeAssembled(nextModelInLine.getName());
    }

    return timeRemainingBeforeAssemblingModel;
  }

  private ProductionTime computeTimeToAssembleEntireCycle() {
    ProductionTime timeToAssemblyEntireCycle = new ProductionTime(0);
    for (Model model : modelAssemblyCycle) {
      timeToAssemblyEntireCycle = timeToAssemblyEntireCycle.add(model.getProductionTime());
    }

    return timeToAssemblyEntireCycle;
  }

  @Override
  public List<Order> getActiveOrders() {
    return new ArrayList<>(orderQueue);
  }

  private void sendFirstModelToBeAssembled() {
    String firstModelTypeToAssemble = modelAssemblyCycle.get(0).getName();
    Model model =
        modelAssemblyCycle.stream()
            .filter(model1 -> model1.getName().equals(firstModelTypeToAssemble))
            .collect(Collectors.toList())
            .get(0);
    ModelOrder modelOrder =
        modelOrderFactory.create(firstModelTypeToAssemble, model.getProductionTime());

    modelAssemblyLineAdapter.addOrder(modelOrder);
    currentModelBeingAssembled = modelOrder;
    currentModelRemainingProductionTime = model.getProductionTime();
  }

  private void sendNextModelToBeAssembled() {
    Model nextModelTypeToAssemble =
        computeNextModelTypeToBeAssembled(currentModelBeingAssembled.getModelType());
    Model model =
        modelAssemblyCycle.stream()
            .filter(model1 -> model1.getName().equals(nextModelTypeToAssemble.getName()))
            .collect(Collectors.toList())
            .get(0);
    ModelOrder modelToAssemble =
        modelOrderFactory.create(nextModelTypeToAssemble.getName(), model.getProductionTime());

    modelAssemblyLineAdapter.addOrder(modelToAssemble);
    currentModelBeingAssembled = modelToAssemble;
    currentModelRemainingProductionTime = nextModelTypeToAssemble.getProductionTime();
  }

  private Model computeNextModelTypeToBeAssembled(String currentModelTypeToAssemble) {
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
        .filter(index -> modelAssemblyCycle.get(index).getName().equals(currentModelType))
        .findFirst()
        .orElse(-1);
  }

  private boolean isLastModel(int modelPositionInModelAssemblyOrder) {
    return modelPositionInModelAssemblyOrder == modelAssemblyCycle.size() - 1;
  }
}
