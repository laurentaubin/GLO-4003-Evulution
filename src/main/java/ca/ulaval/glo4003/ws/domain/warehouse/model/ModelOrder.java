package ca.ulaval.glo4003.ws.domain.warehouse.model;

import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

public class ModelOrder {
  private final OrderId orderId;
  private final String modelType;
  private final AssemblyTime assemblyTime;

  public ModelOrder(OrderId id, String modelType, AssemblyTime assemblyTime) {
    this.orderId = id;
    this.modelType = modelType;
    this.assemblyTime = assemblyTime;
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public String getModelType() {
    return modelType;
  }

  public AssemblyTime getAssemblyTime() {
    return assemblyTime;
  }
}
