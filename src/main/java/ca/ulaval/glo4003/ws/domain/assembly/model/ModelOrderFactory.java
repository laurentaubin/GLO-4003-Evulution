package ca.ulaval.glo4003.ws.domain.assembly.model;

import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import java.util.UUID;

public class ModelOrderFactory {
  public ModelOrder create(String modelType, AssemblyTime assemblyTime) {
    OrderId orderId = new OrderId(UUID.randomUUID().toString());
    return new ModelOrder(orderId, modelType, assemblyTime);
  }
}
