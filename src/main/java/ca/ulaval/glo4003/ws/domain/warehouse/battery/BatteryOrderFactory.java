package ca.ulaval.glo4003.ws.domain.warehouse.battery;

import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;

import java.util.UUID;

public class BatteryOrderFactory {
  public BatteryOrder create(String batteryType, AssemblyTime assemblyTime) {
    OrderId orderId = new OrderId(UUID.randomUUID().toString());
    return new BatteryOrder(orderId, batteryType, assemblyTime);
  }
}
