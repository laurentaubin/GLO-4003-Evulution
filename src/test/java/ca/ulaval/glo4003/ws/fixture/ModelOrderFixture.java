package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

public class ModelOrderFixture {
  private OrderId orderId = new OrderId("anOrderId");
  private String modelName = "a name";
  private AssemblyTime assemblyTime = new AssemblyTime(1);

  public ModelOrderFixture withOrderId(OrderId orderId) {
    this.orderId = orderId;
    return this;
  }

  public ModelOrderFixture withModelName(String modelName) {
    this.modelName = modelName;
    return this;
  }

  public ModelOrderFixture withAssemblyTime(AssemblyTime assemblyTime) {
    this.assemblyTime = assemblyTime;
    return this;
  }

  public ModelOrder build() {
    return new ModelOrder(orderId, modelName, assemblyTime);
  }
}
