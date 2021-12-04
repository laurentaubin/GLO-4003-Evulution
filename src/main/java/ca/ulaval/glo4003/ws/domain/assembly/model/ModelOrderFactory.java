package ca.ulaval.glo4003.ws.domain.assembly.model;

import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.util.UUID;

public class ModelOrderFactory {
  public ModelOrder create(String modelType, ProductionTime productionTime) {
    OrderId orderId = new OrderId(UUID.randomUUID().toString());
    return new ModelOrder(orderId, modelType, productionTime);
  }
}
