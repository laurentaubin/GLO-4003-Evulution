package ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyObservable;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccumulateModelAssemblyLineStrategy extends ModelAssemblyObservable
    implements ModelAssemblyLineStrategy {
  private static final Logger LOGGER = LogManager.getLogger();

  private ModelOrder currentModelBeingAssembled;
  private List<Model> modelAssemblyOrder;
  private final ModelAssemblyLineAdapter modelAssemblyLineAdapter;
  private final ModelInventory modelInventory;
  private final ModelOrderFactory modelOrderFactory;
  private final List<Order> orderQueue = new ArrayList<>();

  public AccumulateModelAssemblyLineStrategy(
      List<Model> modelAssemblyOrder,
      ModelAssemblyLineAdapter modelAssemblyLineAdapter,
      ModelInventory modelInventory,
      ModelOrderFactory modelOrderFactory) {
    this.modelAssemblyOrder = modelAssemblyOrder;
    this.modelAssemblyLineAdapter = modelAssemblyLineAdapter;
    this.modelInventory = modelInventory;
    this.modelOrderFactory = modelOrderFactory;
    sendFirstModelToBeAssembled();
  }

  public void advance() {
    modelAssemblyLineAdapter.advance();
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
            "Model order received for order %s and model type is %s",
            order.getId(), order.getModel().getName()));
    String modelType = order.getModel().getName();
    if (modelInventory.isInStock(modelType)) {
      LOGGER.info(String.format("Model for order %s already in stock", order.getId()));
      modelInventory.removeOne(order.getModel().getName());
      notifyModelAssembled(order);
    } else {
      LOGGER.info(
          String.format(
              "Model for order %s not in stock, adding to order to queue", order.getId()));
      orderQueue.add(order);
    }
  }

  // TODO Implement and write tests
  @Override
  public ProductionTime computeRemainingTimeToProduce(OrderId orderId) {
    return null;
  }

  private void sendFirstModelToBeAssembled() {
    String firstModelTypeToAssemble = modelAssemblyOrder.get(0).getName();
    ModelOrder modelOrder = modelOrderFactory.create(firstModelTypeToAssemble);
    modelAssemblyLineAdapter.addOrder(modelOrder);
    currentModelBeingAssembled = modelOrder;
  }

  private void sendNextModelToBeAssembled() {
    String nextModelTypeToAssemble =
        computeNextModelTypeToBeAssembled(currentModelBeingAssembled.getModelType());
    ModelOrder modelToAssemble = modelOrderFactory.create(nextModelTypeToAssemble);
    modelAssemblyLineAdapter.addOrder(modelToAssemble);
    currentModelBeingAssembled = modelToAssemble;
  }

  private String computeNextModelTypeToBeAssembled(String currentModelTypeToAssemble) {
    int currentModelPositionInModelAssemblyOrder =
        computeCurrentModelPositionInModelAssemblyOrder(currentModelTypeToAssemble);
    if (isLastModel(currentModelPositionInModelAssemblyOrder)) {
      return modelAssemblyOrder.get(0).getName();
    }
    return modelAssemblyOrder.get(currentModelPositionInModelAssemblyOrder + 1).getName();
  }

  private void notifyFirstOrderWaitingForModelType(String modelType) {
    for (Order order : orderQueue) {
      if (order.getModel().getName().equals(modelType)) {
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
      if (order.getModel().getName().equals(modelType)) {
        return true;
      }
    }
    return false;
  }

  private boolean isCurrentModelAssembled() {
    return modelAssemblyLineAdapter.getAssemblyStatus(currentModelBeingAssembled.getOrderId())
        == AssemblyStatus.ASSEMBLED;
  }

  private int computeCurrentModelPositionInModelAssemblyOrder(String currentModelType) {
    return IntStream.range(0, modelAssemblyOrder.size())
        .filter(index -> modelAssemblyOrder.get(index).getName().equals(currentModelType))
        .findFirst()
        .orElse(-1);
  }

  private boolean isLastModel(int modelPositionInModelAssemblyOrder) {
    return modelPositionInModelAssemblyOrder == modelAssemblyOrder.size() - 1;
  }
}
