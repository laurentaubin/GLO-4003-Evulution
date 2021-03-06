package ca.ulaval.glo4003.ws.domain.manufacturer.model;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.manufacturer.PeriodicManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.infrastructure.manufacturer.model.exception.InvalidModelQuantityInQueueException;
import java.util.ArrayList;
import java.util.List;

public class ModelManufacturerImpl extends ModelAssembledObservable
    implements ModelManufacturer, PeriodicManufacturer {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final AssemblyTime ONE_WEEK = new AssemblyTime(1);

  List<ModelOrder> modelOrders = new ArrayList<>();

  private ModelOrder currentModelOrder;
  private AssemblyTime currentModelRemainingAssemblyTime;
  private final ModelAssemblyLineAdapter modelAssemblyLineAdapter;

  public ModelManufacturerImpl() {
    this(serviceLocator.resolve(ModelAssemblyLineAdapter.class));
  }

  public ModelManufacturerImpl(ModelAssemblyLineAdapter modelAssemblyLineAdapter) {
    this.modelAssemblyLineAdapter = modelAssemblyLineAdapter;
  }

  @Override
  public void advanceTime() {
    modelAssemblyLineAdapter.advance();
    if (currentModelOrder != null) {
      processCurrentOrder();
    }
  }

  @Override
  public void stop() {
    currentModelOrder = null;
    currentModelRemainingAssemblyTime = null;
    modelOrders.clear();
  }

  @Override
  public void addOrder(ModelOrder modelOrder) {
    if (currentModelOrder == null) {
      currentModelOrder = modelOrder;
      currentModelRemainingAssemblyTime = new AssemblyTime(modelOrder.getAssemblyTime());
      modelAssemblyLineAdapter.addOrder(currentModelOrder);
    } else {
      modelOrders.add(modelOrder);
    }
  }

  @Override
  public AssemblyTime computeRemainingTimeToProduceNextModelType(String modelType) {
    if (currentModelRemainingAssemblyTime == null) {
      return new AssemblyTime(0);
    }
    AssemblyTime remainingAssemblyTime = new AssemblyTime(currentModelRemainingAssemblyTime);
    if (currentModelOrder.getModelType().equals(modelType)) {
      return remainingAssemblyTime;
    }
    for (ModelOrder modelOrder : modelOrders) {
      remainingAssemblyTime = remainingAssemblyTime.add(modelOrder.getAssemblyTime());
      if (modelOrder.getModelType().equals(modelType)) {
        break;
      }
    }
    return remainingAssemblyTime;
  }

  @Override
  public AssemblyTime computeTimeToProduceQuantityOfModel(Integer quantity, String modelType) {
    AssemblyTime totalAssemblyTime = currentModelRemainingAssemblyTime;
    int numberOfModelsEncountered = 0;
    if (modelType.equals(currentModelOrder.getModelType())) {
      if (quantity == 1) {
        return totalAssemblyTime;
      }
      numberOfModelsEncountered++;
    }

    for (ModelOrder modelOrder : modelOrders) {
      totalAssemblyTime = totalAssemblyTime.add(modelOrder.getAssemblyTime());
      if (modelOrder.getModelType().equals(modelType)) {
        numberOfModelsEncountered++;
      }
    }

    if (numberOfModelsEncountered != quantity) {
      throw new InvalidModelQuantityInQueueException();
    }

    return totalAssemblyTime;
  }

  private void processCurrentOrder() {
    currentModelRemainingAssemblyTime = currentModelRemainingAssemblyTime.subtract(ONE_WEEK);
    if (isCurrentOrderAssembled()) {
      notifyModelAssembled(currentModelOrder);
      sendNextOrder();
    }
  }

  private void sendNextOrder() {
    if (!modelOrders.isEmpty()) {
      currentModelOrder = modelOrders.remove(0);
      currentModelRemainingAssemblyTime = new AssemblyTime(currentModelOrder.getAssemblyTime());
      modelAssemblyLineAdapter.addOrder(currentModelOrder);
    } else {
      currentModelOrder = null;
      currentModelRemainingAssemblyTime = null;
    }
  }

  private boolean isCurrentOrderAssembled() {
    return modelAssemblyLineAdapter.getAssemblyStatus(currentModelOrder.getOrderId())
        == AssemblyStatus.ASSEMBLED;
  }
}
