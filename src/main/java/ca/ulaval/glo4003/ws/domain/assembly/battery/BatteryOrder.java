package ca.ulaval.glo4003.ws.domain.assembly.battery;

import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;

public class BatteryOrder {
  private final OrderId orderId;
  private final String batteryType;
  private final ProductionTime productionTime;

  public BatteryOrder(OrderId orderId, String batteryType, ProductionTime productionTime) {
    this.orderId = orderId;
    this.batteryType = batteryType;
    this.productionTime = productionTime;
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public String getBatteryType() {
    return batteryType;
  }

  public ProductionTime getProductionTime() {
    return productionTime;
  }
}
