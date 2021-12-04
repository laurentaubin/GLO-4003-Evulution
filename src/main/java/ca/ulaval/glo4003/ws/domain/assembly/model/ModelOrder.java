package ca.ulaval.glo4003.ws.domain.assembly.model;

import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;

public class ModelOrder {
  private final OrderId orderId;
  private final String modelType;
  private final ProductionTime productionTime;

  public ModelOrder(OrderId id, String modelType, ProductionTime productionTime) {
    this.orderId = id;
    this.modelType = modelType;
    this.productionTime = productionTime;
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public String getModelType() {
    return modelType;
  }

  public ProductionTime getProductionTime() {
    return productionTime;
  }
}
