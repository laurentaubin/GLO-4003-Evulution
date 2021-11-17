package ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model;

import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;

public class ModelOrder {
  private final OrderId orderId;
  private final String modelType;

  public ModelOrder(OrderId orderId, String modelType) {
    this.orderId = orderId;
    this.modelType = modelType;
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public String getModelType() {
    return modelType;
  }
}
