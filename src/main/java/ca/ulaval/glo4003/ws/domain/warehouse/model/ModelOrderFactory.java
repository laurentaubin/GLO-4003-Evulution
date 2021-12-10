package ca.ulaval.glo4003.ws.domain.warehouse.model;

import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import java.util.UUID;

public class ModelOrderFactory {
  public ModelOrder create(String modelType, AssemblyTime assemblyTime) {
    OrderId orderId = new OrderId(UUID.randomUUID().toString());
    return new ModelOrder(orderId, modelType, assemblyTime);
  }
}
