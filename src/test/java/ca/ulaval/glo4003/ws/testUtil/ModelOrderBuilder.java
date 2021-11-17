package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model.ModelOrder;

public class ModelOrderBuilder {
  private OrderId orderId = new OrderId("anOrderId");
  private String modelName = "a name";

  public ModelOrderBuilder withOrderId(OrderId orderId) {
    this.orderId = orderId;
    return this;
  }

  public ModelOrderBuilder withModelName(String modelName) {
    this.modelName = modelName;
    return this;
  }

  public ModelOrder build() {
    return new ModelOrder(orderId, modelName);
  }
}
