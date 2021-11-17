package ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model;

import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import java.util.UUID;

public class ModelOrderFactory {
  public ModelOrder create(String modelType) {
    OrderId orderId = new OrderId(UUID.randomUUID().toString());
    return new ModelOrder(orderId, modelType);
  }
}
