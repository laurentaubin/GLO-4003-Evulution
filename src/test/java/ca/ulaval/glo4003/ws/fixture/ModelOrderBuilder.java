package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

public class ModelOrderBuilder {
  private OrderId orderId = new OrderId("anOrderId");
  private String modelName = "a name";
  private AssemblyTime assemblyTime = new AssemblyTime(1);

  public ModelOrderBuilder withOrderId(OrderId orderId) {
    this.orderId = orderId;
    return this;
  }

  public ModelOrderBuilder withModelName(String modelName) {
    this.modelName = modelName;
    return this;
  }

  public ModelOrderBuilder withAssemblyTime(AssemblyTime assemblyTime) {
    this.assemblyTime = assemblyTime;
    return this;
  }

  public ModelOrder build() {
    return new ModelOrder(orderId, modelName, assemblyTime);
  }
}
