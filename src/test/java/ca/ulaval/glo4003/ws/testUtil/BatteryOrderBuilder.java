package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;

public class BatteryOrderBuilder {
  private OrderId orderId = new OrderId("anOrderId");
  private String batteryType = "a type";
  private ProductionTime productionTime = new ProductionTime(1);

  public BatteryOrderBuilder withOrderId(OrderId orderId) {
    this.orderId = orderId;
    return this;
  }

  public BatteryOrderBuilder withModelName(String modelName) {
    this.batteryType = modelName;
    return this;
  }

  public BatteryOrderBuilder withProductionTime(ProductionTime productionTime) {
    this.productionTime = productionTime;
    return this;
  }

  public BatteryOrder build() {
    return new BatteryOrder(orderId, batteryType, productionTime);
  }
}
