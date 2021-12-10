package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

public class BatteryOrderFixture {
  private OrderId orderId = new OrderId("anOrderId");
  private String batteryType = "a type";
  private AssemblyTime assemblyTime = new AssemblyTime(1);

  public BatteryOrderFixture withOrderId(OrderId orderId) {
    this.orderId = orderId;
    return this;
  }

  public BatteryOrderFixture withBatteryType(String batteryType) {
    this.batteryType = batteryType;
    return this;
  }

  public BatteryOrderFixture withAssemblyTime(AssemblyTime assemblyTime) {
    this.assemblyTime = assemblyTime;
    return this;
  }

  public BatteryOrder build() {
    return new BatteryOrder(orderId, batteryType, assemblyTime);
  }
}
