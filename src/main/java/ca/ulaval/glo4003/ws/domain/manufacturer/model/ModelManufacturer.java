package ca.ulaval.glo4003.ws.domain.manufacturer.model;

import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

public interface ModelManufacturer {
  void addOrder(ModelOrder modelOrder);

  AssemblyTime computeRemainingTimeToProduceNextModelType(String modelType);

  AssemblyTime computeTimeToProduceQuantityOfModel(Integer quantity, String modelType);
}
