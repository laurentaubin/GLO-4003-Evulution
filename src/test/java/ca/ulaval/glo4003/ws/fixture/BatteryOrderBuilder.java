package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;

public class BatteryOrderBuilder {
  private OrderId orderId = new OrderId("anOrderId");
  private String batteryType = "a type";
  private AssemblyTime assemblyTime = new AssemblyTime(1);

  public BatteryOrderBuilder withOrderId(OrderId orderId) {
    this.orderId = orderId;
    return this;
  }

  public BatteryOrderBuilder withModelName(String modelName) {
    this.batteryType = modelName;
    return this;
  }

  public BatteryOrderBuilder withAssemblyTime(AssemblyTime assemblyTime) {
    this.assemblyTime = assemblyTime;
    return this;
  }

  public BatteryOrder build() {
    return new BatteryOrder(orderId, batteryType, assemblyTime);
  }
}
