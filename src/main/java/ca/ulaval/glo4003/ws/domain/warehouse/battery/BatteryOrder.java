package ca.ulaval.glo4003.ws.domain.warehouse.battery;

import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

public class BatteryOrder {
  private final OrderId orderId;
  private final String batteryType;
  private final AssemblyTime assemblyTime;

  public BatteryOrder(OrderId orderId, String batteryType, AssemblyTime assemblyTime) {
    this.orderId = orderId;
    this.batteryType = batteryType;
    this.assemblyTime = assemblyTime;
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public String getBatteryType() {
    return batteryType;
  }

  public AssemblyTime getAssemblyTime() {
    return assemblyTime;
  }
}
